package com.Projet.forum;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.Projet.forum.databinding.ActivityNewPostBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private ActivityNewPostBinding binding;
    private User currentUser;
    private String editPostId;

    private List<String> categoriesList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    // Theme Colors
    private final int GOLD_COLOR = Color.parseColor("#FFD700");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        editPostId = getIntent().getStringExtra("editPostId");

        // 1. Setup Toolbar (matches your XML id: toolbar)
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(editPostId != null ? "Modifier le post" : "Nouveau Post");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setTitleTextColor(GOLD_COLOR);

        // 2. Setup Category Dropdown (matches your XML id: autoCompleteCategory)
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoriesList);
        binding.autoCompleteCategory.setAdapter(categoryAdapter);

        // Ensure dropdown appears on click
        binding.autoCompleteCategory.setOnClickListener(v -> binding.autoCompleteCategory.showDropDown());

        // 3. Load Data
        loadCategoriesFromFirestore();

        if (editPostId != null) {
            loadPostForEdit();
        }

        // 4. Save Button (matches your XML id: buttonPublish)
        binding.buttonPublish.setOnClickListener(v -> savePost());
    }

    private void loadCategoriesFromFirestore() {
        FirebaseFirestore.getInstance().collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoriesList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null) categoriesList.add(name);
                    }
                    categoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching categories", e));
    }

    private void loadPostForEdit() {
        FirebaseFirestore.getInstance().collection("posts").document(editPostId).get()
                .addOnSuccessListener(doc -> {
                    Post p = doc.toObject(Post.class);
                    if (p != null) {
                        binding.editTextTitle.setText(p.getTitle());
                        binding.editTextContent.setText(p.getContent());
                        binding.autoCompleteCategory.setText(p.getCategory(), false);
                    }
                });
    }

    private void savePost() {
        String rawTitle = binding.editTextTitle.getText().toString().trim();
        String rawContent = binding.editTextContent.getText().toString().trim();
        String selectedCategory = binding.autoCompleteCategory.getText().toString();

        if (rawTitle.isEmpty() || rawContent.isEmpty() || selectedCategory.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Apply Profanity Filter
        String filteredTitle = ProfanityFilter.filter(rawTitle);
        String filteredContent = ProfanityFilter.filter(rawContent);

        if (editPostId != null) {
            // Update Existing Post
            FirebaseFirestore.getInstance().collection("posts").document(editPostId)
                    .update("title", filteredTitle,
                            "content", filteredContent,
                            "category", selectedCategory)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Post mis à jour", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show());
        } else {
            // Create New Post
            Post post = new Post(
                    UUID.randomUUID().toString(),
                    currentUser,
                    filteredTitle,
                    filteredContent,
                    System.currentTimeMillis(),
                    selectedCategory
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
