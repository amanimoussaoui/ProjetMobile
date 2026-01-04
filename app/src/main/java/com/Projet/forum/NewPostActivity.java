package com.Projet.forum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.Projet.forum.databinding.ActivityNewPostBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private ActivityNewPostBinding binding;
    private User currentUser;
    private String editPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        editPostId = getIntent().getStringExtra("editPostId");

        setSupportActionBar(binding.toolbar);
        if (editPostId != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Modifier le post");
            }
            loadPostForEdit();
        }

        binding.buttonPublish.setOnClickListener(v -> savePost());
    }

    private void loadPostForEdit() {
        FirebaseFirestore.getInstance().collection("posts").document(editPostId).get()
                .addOnSuccessListener(doc -> {
                    Post p = doc.toObject(Post.class);
                    if (p != null) {
                        binding.editTextTitle.setText(p.getTitle());
                        binding.editTextContent.setText(p.getContent());
                    }
                });
    }

    private void savePost() {
        // 1. Get raw text
        String rawTitle = binding.editTextTitle.getText().toString().trim();
        String rawContent = binding.editTextContent.getText().toString().trim();

        if (rawTitle.isEmpty() || rawContent.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Apply Profanity Filter
        String filteredTitle = ProfanityFilter.filter(rawTitle);
        String filteredContent = ProfanityFilter.filter(rawContent);

        if (editPostId != null) {
            // Update mode
            FirebaseFirestore.getInstance().collection("posts").document(editPostId)
                    .update("title", filteredTitle, "content", filteredContent)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(this, "Post mis à jour", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            // Create mode
            Post post = new Post(
                    UUID.randomUUID().toString(),
                    currentUser,
                    filteredTitle,
                    filteredContent,
                    System.currentTimeMillis()
            );
            FirebaseStorageHelper.getInstance().addPost(post, success -> {
                if (success) {
                    Toast.makeText(this, "Post publié", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Erreur lors de la publication", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
