package com.Projet.forum;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.Projet.forum.databinding.ActivityAddCommentBinding;
import java.util.UUID;

public class AddCommentActivity extends AppCompatActivity {

    private ActivityAddCommentBinding binding;
    private User currentUser;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postId = getIntent().getStringExtra("postId");
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
                filteredText, // Use filtered text here
                System.currentTimeMillis()
        );

        // 1. Update the Main Post Document
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("posts")
                .document(postId)
                .update("comments", com.google.firebase.firestore.FieldValue.arrayUnion(comment))
                .addOnSuccessListener(aVoid -> {
                    // 2. Also add to sub-collection for backup/detail view safety
                    FirebaseStorageHelper.getInstance().addComment(postId, comment, success -> {
                        Toast.makeText(this, "Commentaire ajouté", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show());
    }
}
