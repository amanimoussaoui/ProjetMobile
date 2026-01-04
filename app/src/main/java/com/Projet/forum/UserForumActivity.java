package com.Projet.forum;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Projet.forum.databinding.ActivityForumListBinding;

import java.util.ArrayList;

public class UserForumActivity extends AppCompatActivity {

    private ActivityForumListBinding binding;
    private PostAdapter adapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForumListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);

        // ✅ REQUIRED for PostAdapter
        String currentUserId = currentUser.getId();

        adapter = new PostAdapter(
                this,
                new ArrayList<>(),
                false,              // not admin
                currentUserId,      // 🔥 REQUIRED PARAM
                new PostAdapter.OnPostActionListener() {

                    @Override
                    public void onCommentClick(Post post) {
                        Intent intent = new Intent(
                                UserForumActivity.this,
                                ForumDetailActivity.class
                        );
                        intent.putExtra("postId", post.getPostId());
                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(Post post) {
                        // Users cannot delete posts
                    }

                    @Override
                    public void onLikeClick(Post post) {
                        post.setLikeCount(post.getLikeCount() + 1);
                        FirebaseStorageHelper.getInstance().updatePost(post, success -> {
                            if (!success) {
                                Toast.makeText(
                                        UserForumActivity.this,
                                        "Erreur lors de la mise à jour du post",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                    }
                }
        );

        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPosts.setAdapter(adapter);

        binding.fabNewPost.setOnClickListener(v ->
                startActivity(new Intent(
                        UserForumActivity.this,
                        NewPostActivity.class
                ))
        );

        loadPosts();
    }

    private void loadPosts() {
        FirebaseStorageHelper.getInstance()
                .getAllPosts(posts -> adapter.updatePosts(posts));
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
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
