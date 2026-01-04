package com.Projet.forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Projet.forum.databinding.ActivityForumListBinding;

public class AdminForumActivity extends AppCompatActivity {

    private ActivityForumListBinding binding;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForumListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if current user is admin
        User currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            startActivity(new Intent(this, ForumListActivity.class)); // redirect if not admin
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Forum (Admin)");
        }

        // Initialize adapter with empty list first
        String currentUserId = FirebaseStorageHelper.getInstance().getCurrentUser() != null
                ? FirebaseStorageHelper.getInstance().getCurrentUser().getId()
                : "";

        adapter = new PostAdapter(
                this,
                new ArrayList<>(),
                true,
                currentUserId,
                new PostAdapter.OnPostActionListener() {

                    @Override
                    public void onCommentClick(Post post) {
                        Intent intent = new Intent(AdminForumActivity.this, ForumDetailActivity.class);
                        intent.putExtra("postId", post.getPostId());
                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(Post post) {
                        FirebaseStorageHelper.getInstance().deletePost(post.getPostId(), success -> {
                            if (success) {
                                Toast.makeText(AdminForumActivity.this, "Post supprimé", Toast.LENGTH_SHORT).show();
                                loadPosts();
                            } else {
                                Toast.makeText(AdminForumActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onLikeClick(Post post) {
                        post.setLikeCount(post.getLikeCount() + 1);
                        FirebaseStorageHelper.getInstance().updatePost(post, success -> {
                            if (success) {
                                loadPosts();
                            } else {
                                Toast.makeText(AdminForumActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );



        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPosts.setAdapter(adapter);

        // FAB to create new post
        binding.fabNewPost.setOnClickListener(v -> startActivity(new Intent(this, NewPostActivity.class)));

        // Load posts from Firebase
        loadPosts();
    }

    private void loadPosts() {
        FirebaseStorageHelper.getInstance().getAllPosts(posts -> {
            adapter.updatePosts(posts);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forum, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseStorageHelper.getInstance().logout();
            startActivity(new Intent(this, ForumListActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
