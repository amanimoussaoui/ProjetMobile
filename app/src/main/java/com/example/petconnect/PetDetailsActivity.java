package com.example.petconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class PetDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PetDetailsActivity";

    private ImageView petPhoto;
    private TextView petEmoji, petName, petAge, petGender, petBreed, petAbout;
    private LinearLayout requirementsContainer;
    private Button adoptButton;
    private ImageView backButton, favoriteButton;
    private Pet pet;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_details);

        // Récupérer l'animal depuis l'Intent
        pet = (Pet) getIntent().getSerializableExtra("pet");

        if (pet == null) {
            Log.e(TAG, "❌ No pet data received!");
            finish();
            return;
        }

        Log.d(TAG, "📌 Pet details for: " + pet.getName());
        Log.d(TAG, "📸 Photo URL: " + pet.getPhotoPath());

        // Initialize views
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        petPhoto = findViewById(R.id.petPhotoDetails);
        petEmoji = findViewById(R.id.petEmojiDetails);
        petName = findViewById(R.id.petNameDetails);
        petAge = findViewById(R.id.petAgeDetails);
        petGender = findViewById(R.id.petGenderDetails);
        petBreed = findViewById(R.id.petBreedDetails);
        petAbout = findViewById(R.id.petAboutText);
        requirementsContainer = findViewById(R.id.requirementsContainer);
        adoptButton = findViewById(R.id.adoptButton);

        // Afficher les informations
        displayPetInfo();

        // Handle back button with animation (optimisé pour transition rapide)
        backButton.setOnClickListener(v -> {
            v.setEnabled(false);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
        });

        favoriteButton.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            favoriteButton.setImageResource(isFavorite ?
                    android.R.drawable.btn_star_big_on :
                    android.R.drawable.btn_star_big_off);
        });

        adoptButton.setOnClickListener(v -> {
            v.setEnabled(false);
            Intent intent = new Intent(PetDetailsActivity.this, AdoptionFormActivity.class);
            intent.putExtra("pet", pet);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            v.postDelayed(() -> v.setEnabled(true), 300);
        });
    }

    private void displayPetInfo() {
        // ✅ CHARGER L'IMAGE AVEC GLIDE
        String photoUrl = pet.getPhotoPath();

        if (photoUrl != null && !photoUrl.isEmpty()) {
            // Il y a une URL → Charger l'image
            Log.d(TAG, "✅ Loading image from URL: " + photoUrl);

            petEmoji.setVisibility(View.GONE);
            petPhoto.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.color.teal_primary)
                    .error(R.color.teal_primary)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false) // Utiliser le cache mémoire pour chargement instantané
                    .centerCrop()
                    .thumbnail(0.1f) // Afficher une miniature rapidement pendant le chargement
                    .into(petPhoto);

        } else {
            // Pas d'URL → Afficher l'emoji
            Log.d(TAG, "⚠️ No photo URL, showing emoji");

            petPhoto.setVisibility(View.GONE);
            petEmoji.setVisibility(View.VISIBLE);
            petEmoji.setText(pet.getEmoji());
        }

        petName.setText(pet.getName());
        petAge.setText(pet.getAge());
        petGender.setText(pet.getGender());
        petBreed.setText(pet.getBreed());
        petAbout.setText(pet.getAbout());

        // Add requirements dynamically
        for (String requirement : pet.getRequirements()) {
            android.view.View requirementView = getLayoutInflater().inflate(
                    R.layout.item_requirement, requirementsContainer, false);
            TextView requirementText = requirementView.findViewById(R.id.requirementText);
            requirementText.setText(requirement);
            requirementsContainer.addView(requirementView);
        }
    }
}