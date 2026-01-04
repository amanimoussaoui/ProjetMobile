package com.Projet.forum;

import android.content.Intent;
import android.os.Bundle;import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Projet.forum.databinding.ActivityForumListBinding;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ForumListActivity extends AppCompatActivity {

    private ActivityForumListBinding binding;
    private PostAdapter adapter;
    private ListenerRegistration postsListener; // ✅ Essential for real-time tracking
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForumListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // 1. Prepare User Data
        User currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        currentUserId = (currentUser != null) ? currentUser.getId() : "";
        boolean isAdmin = (currentUser != null) && currentUser.isAdmin();

        // 2. Initialize Adapter
        adapter = new PostAdapter(
                this,
                null,
                isAdmin,
                currentUserId,
                new PostAdapter.OnPostActionListener() {
                    @Override
                    public void onCommentClick(Post post) {
                        Intent intent = new Intent(ForumListActivity.this, ForumDetailActivity.class);
                        intent.putExtra("postId", post.getPostId());
                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(Post post) {
                        FirebaseStorageHelper.getInstance().deletePost(post.getPostId(), success -> {
                            if (success) Toast.makeText(ForumListActivity.this, "Post supprimé", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onLikeClick(Post post) {
                        if (currentUserId.isEmpty()) return;

                        // Use arrayUnion to ensure likes are unique and atomic
                        FirebaseFirestore.getInstance()
                                .collection("posts")
                                .document(post.getPostId())
                                .update("likedUserIds", FieldValue.arrayUnion(currentUserId))
                                .addOnFailureListener(e -> Toast.makeText(ForumListActivity.this, "Erreur Like", Toast.LENGTH_SHORT).show());
                    }
                }
        );

        // 3. Setup RecyclerView
        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPosts.setAdapter(adapter);

        binding.fabNewPost.setOnClickListener(v ->
                startActivity(new Intent(this, NewPostActivity.class))
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ✅ Start listening when the activity becomes visible
        startListeningForPosts();
    }

    private void startListeningForPosts() {
        // Clean up existing listener to prevent duplicates
        if (postsListener != null) {
            postsListener.remove();
        }

        // Use the snapshot listener from your helper
        postsListener = FirebaseStorageHelper.getInstance().listenToPosts(posts -> {
            if (posts != null) {
                // This updates the adapter immediately when ANY user likes or comments
                adapter.updatePosts(posts);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ✅ Stop listening when invisible to save battery and data
        if (postsListener != null) {
            postsListener.remove();
            postsListener = null;
        }
    }

    // Note: No need for onResume or loadPosts() anymore.
    // The SnapshotListener handles everything automatically.
}
