package com.Projet.forum;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.Projet.forum.databinding.ActivityAddCommentBinding;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCommentActivity extends AppCompatActivity {

    private ActivityAddCommentBinding binding;
    private User currentUser;
    private String postId;
    private String postAuthorId; // ✅ Added for Feature 5
    private String postTitle;    // ✅ Added for Feature 5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Get Intent Extras from ForumDetailActivity
        postId = getIntent().getStringExtra("postId");
        postAuthorId = getIntent().getStringExtra("postAuthorId");
        postTitle = getIntent().getStringExtra("postTitle");

        if (postId == null) {
            Toast.makeText(this, "Post introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vous devez être connecté pour commenter", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.buttonSendComment.setOnClickListener(v -> sendComment());
    }

    private void sendComment() {
        String rawCommentText = binding.editTextComment.getText().toString().trim();
        if (rawCommentText.isEmpty()) return;

        // ✅ Apply Profanity Filter
        String filteredText = ProfanityFilter.filter(rawCommentText);

        Comment comment = new Comment(
                UUID.randomUUID().toString(),
                postId,
                currentUser,
                filteredText,
                System.currentTimeMillis()
        );

        // 1. Update the Main Post Document
        FirebaseFirestore.getInstance()
                .collection("posts")
                .document(postId)
                .update("comments", FieldValue.arrayUnion(comment))
                .addOnSuccessListener(aVoid -> {

                    // ✅ Feature 5: Trigger Notification Bridge
                    sendCommentNotification();

                    // 2. Also add to sub-collection for backup/detail view safety
                    FirebaseStorageHelper.getInstance().addComment(postId, comment, success -> {
                        Toast.makeText(this, "Commentaire ajouté", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show());
    }

    /**
     * ✅ Feature 5: Automated Notification Bridge for Comments
     */
    private void sendCommentNotification() {
        // Only notify if we have a target author and it's not our own post
        if (postAuthorId != null && !postAuthorId.equals(currentUser.getId())) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("targetUserId", postAuthorId);
            notif.put("senderId", currentUser.getId());
            notif.put("title", "Nouveau Commentaire !");
            notif.put("message", currentUser.getName() + " a commenté : " + (postTitle != null ? postTitle : "votre post"));
            notif.put("timestamp", System.currentTimeMillis());
            notif.put("isRead", false);

            FirebaseFirestore.getInstance().collection("notifications")
                    .add(notif)
                    .addOnSuccessListener(doc -> Log.d("NOTIF", "Comment notification sent to " + postAuthorId))
                    .addOnFailureListener(e -> Log.e("NOTIF", "Failed to send notif", e));
        }
    }
}
