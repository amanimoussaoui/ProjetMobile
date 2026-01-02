package com.example.petconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petconnect.models.User;
import com.example.petconnect.utils.FirebaseAuthManager;
import com.example.petconnect.utils.PetMatchingAlgorithm;
import com.example.petconnect.utils.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView petsRecyclerView;
    private FirebaseAuthManager authManager;
    private UserManager userManager;
    private TextView matchingRecommendationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        authManager = FirebaseAuthManager.getInstance();
        userManager = UserManager.getInstance();

        // Vérifier si l'utilisateur est connecté
        if (!authManager.isUserLoggedIn()) {
            // Rediriger vers LoginActivity si non connecté
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        petsRecyclerView = findViewById(R.id.petsRecyclerView);
        matchingRecommendationText = findViewById(R.id.matchingRecommendationText);

        // Configuration de la BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Configuration de la grille d'animaux
        setupPetsGrid();

        // Bouton "See More"
        TextView seeMoreButton = findViewById(R.id.seeMoreButton);
        if (seeMoreButton != null) {
            seeMoreButton.setOnClickListener(v -> {
                // TODO: Implémenter la navigation vers la page complète des animaux
                Toast.makeText(this, "Fonctionnalité à venir", Toast.LENGTH_SHORT).show();
            });
        }
        
        // Bouton Chatbot (FloatingActionButton)
        FloatingActionButton chatbotButton = findViewById(R.id.chatbotButton);
        if (chatbotButton != null) {
            chatbotButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ChatbotActivity.class);
                startActivity(intent);
            });
        }
        
        // Charger les recommandations de matching
        loadMatchingRecommendations();
    }

    private void setupPetsGrid() {
        if (petsRecyclerView != null) {
            // Pour l'instant, on utilise un GridView simple
            // Vous pouvez remplacer par un RecyclerView avec un adapter plus tard
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            petsRecyclerView.setLayoutManager(layoutManager);
            // TODO: Ajouter un adapter pour les animaux
        }
    }
    
    private void loadMatchingRecommendations() {
        if (authManager.getCurrentUser() == null) {
            return;
        }
        
        String userId = authManager.getCurrentUser().getUid();
        userManager.getUser(userId, new UserManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                // Générer les recommandations
                String bestMatch = PetMatchingAlgorithm.getBestMatch(user);
                int score = PetMatchingAlgorithm.calculateCompatibilityScore(user, bestMatch);
                String level = PetMatchingAlgorithm.getCompatibilityLevel(score);
                
                // Afficher la recommandation
                if (matchingRecommendationText != null) {
                    String recommendation = "💡 Recommandation: " + bestMatch.toUpperCase() + 
                                          " (" + level + " - " + score + "%)";
                    matchingRecommendationText.setText(recommendation);
                    matchingRecommendationText.setVisibility(android.view.View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Ne rien afficher en cas d'erreur
            }
        });
    }
}


