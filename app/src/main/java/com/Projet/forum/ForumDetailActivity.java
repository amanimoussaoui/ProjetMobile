package com.Projet.forum;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.Projet.forum.databinding.ActivityForumDetailBinding;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ForumDetailActivity extends AppCompatActivity {

    private ActivityForumDetailBinding binding;
    private Post post;
    private String currentUserId;
    private boolean isAdmin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForumDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String postId = getIntent().getStringExtra("postId");
        User currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        currentUserId = (currentUser != null) ? currentUser.getId() : "";
        isAdmin = (currentUser != null) && currentUser.isAdmin();

        setupToolbar();
        if (postId != null) loadPostRealTime(postId);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadPostRealTime(String postId) {
        FirebaseFirestore.getInstance().collection("posts").document(postId)
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        post = snapshot.toObject(Post.class);
                        displayPost();
                        setupComments();
                    }
                });
    }

    private void displayPost() {
        if (post == null) return;
        binding.textViewAuthorName.setText(post.getAuthor().getName());
        binding.textViewPostTitle.setText(post.getTitle());
        binding.textViewPostContent.setText(post.getContent());
        binding.buttonLike.setText(String.valueOf(post.getLikeCount()));
        binding.buttonLike.setIconResource(post.isLikedBy(currentUserId) ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        binding.buttonLike.setOnClickListener(v -> handleLike());
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        boolean isOwner = post != null && post.getAuthor() != null && post.getAuthor().getId().equals(currentUserId);

        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem editItem = menu.findItem(R.id.action_edit);

        if (deleteItem != null) deleteItem.setVisible(isAdmin || isOwner);
        if (editItem != null) editItem.setVisible(isOwner);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) { deleteThisPost(); return true; }
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, NewPostActivity.class);
            intent.putExtra("editPostId", post.getPostId());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupComments() {
        binding.recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter adapter = new CommentAdapter(this, post.getComments(), isAdmin, currentUserId, new CommentAdapter.OnCommentActionListener() {
            @Override public void onDeleteClick(Comment comment) { deleteComment(comment); }
            @Override public void onEditClick(Comment comment) { showEditCommentDialog(comment); }
        });
        binding.recyclerViewComments.setAdapter(adapter);
        binding.buttonCommentCount.setText(String.valueOf(post.getComments().size()));
        binding.fabAddComment.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCommentActivity.class);
            intent.putExtra("postId", post.getPostId());
            startActivity(intent);
        });
    }

    private void showEditCommentDialog(Comment oldComment) {
        EditText editText = new EditText(this);
        editText.setText(oldComment.getContent());
        new AlertDialog.Builder(this).setTitle("Modifier commentaire").setView(editText)
                .setPositiveButton("OK", (d, w) -> {
                    // ✅ Apply Profanity Filter before updating
                    String filteredText = ProfanityFilter.filter(editText.getText().toString());

                    Comment updated = new Comment(oldComment.getCommentId(), oldComment.getPostId(), oldComment.getAuthor(), filteredText, oldComment.getTimestamp());

                    FirebaseFirestore.getInstance().collection("posts").document(post.getPostId())
                            .update("comments", FieldValue.arrayRemove(oldComment))
                            .addOnSuccessListener(a -> FirebaseFirestore.getInstance().collection("posts").document(post.getPostId())
                                    .update("comments", FieldValue.arrayUnion(updated)));
                }).setNegativeButton("Annuler", null).show();
    }

    private void handleLike() {
        if (currentUserId.isEmpty()) return;
        FirebaseFirestore.getInstance().collection("posts").document(post.getPostId())
                .update("likedUserIds", post.isLikedBy(currentUserId) ? FieldValue.arrayRemove(currentUserId) : FieldValue.arrayUnion(currentUserId));
    }

    private void deleteThisPost() {
        FirebaseStorageHelper.getInstance().deletePost(post.getPostId(), success -> finish());
    }

    private void deleteComment(Comment comment) {
        FirebaseFirestore.getInstance().collection("posts").document(post.getPostId()).update("comments", FieldValue.arrayRemove(comment));
    }
}
