package com.example.petconnect;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager2 viewPager2;
    private PetPagerAdapter adapter;
    private List<Pet> allPets = new ArrayList<>();
    private List<Pet> filteredPets = new ArrayList<>();

    // UI Components
    private EditText searchEditText;
    private ImageView clearSearchButton;
    private androidx.cardview.widget.CardView sortByLocationButton;
    private androidx.cardview.widget.CardView resetFilterButton;
    private TextView resultsCounter;
    private LinearLayout emptyStateLayout;

    private boolean isSortedByLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "🚀 MainActivity started");

        configureFirestore();
        initializeViews();

        // ⚡ OPTIMISATION: Configuration ViewPager2 AVANT le chargement
        optimizeViewPager2();

        loadPetsFromFirebase();
    }

    /**
     * ⚡ OPTIMISATION: Configuration pour des transitions ultra-rapides
     */
    private void optimizeViewPager2() {
        if (viewPager2 == null) return;

        // Réduire la durée de défilement (par défaut = 250ms, on passe à 100ms pour ultra-rapide)
        try {
            java.lang.reflect.Field mRecyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            mRecyclerViewField.setAccessible(true);
            androidx.recyclerview.widget.RecyclerView recyclerView =
                    (androidx.recyclerview.widget.RecyclerView) mRecyclerViewField.get(viewPager2);

            if (recyclerView != null) {
                java.lang.reflect.Field mScrollDurationField =
                        androidx.recyclerview.widget.RecyclerView.class.getDeclaredField("mScrollDuration");
                mScrollDurationField.setAccessible(true);
                mScrollDurationField.set(recyclerView, 100); // 100ms pour transitions ultra-rapides
            }
        } catch (Exception e) {
            Log.w(TAG, "⚠️ Could not optimize scroll duration: " + e.getMessage());
        }

        // ⚡ Optimisations ViewPager2
        viewPager2.setOffscreenPageLimit(1); // Limiter le pré-chargement (économise la mémoire)

        // Transformer personnalisé pour transitions fluides
        viewPager2.setPageTransformer(new FastPageTransformer());

        Log.d(TAG, "⚡ ViewPager2 optimized for fast transitions");
    }

    private void initializeViews() {
        viewPager2 = findViewById(R.id.viewPagerPets);
        searchEditText = findViewById(R.id.searchEditText);
        clearSearchButton = findViewById(R.id.clearSearchButton);
        sortByLocationButton = findViewById(R.id.sortByLocationButton);
        resetFilterButton = findViewById(R.id.resetFilterButton);
        resultsCounter = findViewById(R.id.resultsCounter);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        if (viewPager2 == null) {
            Log.e(TAG, "❌ ViewPager2 is NULL! Check your layout.");
            Toast.makeText(this, "Erreur: ViewPager introuvable", Toast.LENGTH_LONG).show();
            return;
        }

        setupSearchFunctionality();
        setupFilterButtons();
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                Log.d(TAG, "🔍 Search query: '" + query + "'");
                filterPets(query);
                clearSearchButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        clearSearchButton.setOnClickListener(v -> {
            Log.d(TAG, "❌ Clear search clicked");
            searchEditText.setText("");
        });
    }

    private void setupFilterButtons() {
        sortByLocationButton.setOnClickListener(v -> {
            isSortedByLocation = !isSortedByLocation;
            Log.d(TAG, "📍 Sort by location: " + isSortedByLocation);

            if (isSortedByLocation) {
                sortByLocationButton.setCardBackgroundColor(getResources().getColor(R.color.teal_primary));
                Toast.makeText(this, "✓ Tri par lieu activé", Toast.LENGTH_SHORT).show();
            } else {
                sortByLocationButton.setCardBackgroundColor(0xFFE8F5F3);
                Toast.makeText(this, "Tri désactivé", Toast.LENGTH_SHORT).show();
            }

            filterPets(searchEditText.getText().toString());
        });

        resetFilterButton.setOnClickListener(v -> {
            Log.d(TAG, "🔄 Reset filters clicked");
            searchEditText.setText("");
            isSortedByLocation = false;
            sortByLocationButton.setCardBackgroundColor(0xFFE8F5F3);
            filteredPets.clear();
            filteredPets.addAll(allPets);
            updateUI();
            Toast.makeText(this, "✓ Filtres réinitialisés", Toast.LENGTH_SHORT).show();
        });
    }

    private void filterPets(String query) {
        Log.d(TAG, "🔍 Filtering pets with query: '" + query + "'");
        Log.d(TAG, "📊 Total pets available: " + allPets.size());

        filteredPets.clear();

        if (query == null || query.trim().isEmpty()) {
            Log.d(TAG, "✅ No filter - showing all pets");
            filteredPets.addAll(allPets);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            Log.d(TAG, "🔎 Searching for breed containing: '" + lowerCaseQuery + "'");

            for (Pet pet : allPets) {
                String breed = pet.getBreed();
                if (breed != null && !breed.isEmpty()) {
                    String lowerBreed = breed.toLowerCase();
                    if (lowerBreed.contains(lowerCaseQuery)) {
                        filteredPets.add(pet);
                        Log.d(TAG, "✅ Match found: " + pet.getName() + " - " + breed);
                    }
                } else {
                    Log.w(TAG, "⚠️ Pet " + pet.getName() + " has no breed");
                }
            }

            Log.d(TAG, "📊 Filtered results: " + filteredPets.size() + " pets");
        }

        if (isSortedByLocation && !filteredPets.isEmpty()) {
            Log.d(TAG, "📍 Sorting by location...");
            Collections.sort(filteredPets, new Comparator<Pet>() {
                @Override
                public int compare(Pet p1, Pet p2) {
                    String loc1 = p1.getLocation() != null ? p1.getLocation() : "";
                    String loc2 = p2.getLocation() != null ? p2.getLocation() : "";
                    int result = loc1.compareToIgnoreCase(loc2);
                    Log.d(TAG, "Comparing: " + loc1 + " vs " + loc2 + " = " + result);
                    return result;
                }
            });
            Log.d(TAG, "✅ Sorting complete");
        }

        updateUI();
    }

    private void updateUI() {
        runOnUiThread(() -> {
            Log.d(TAG, "🎨 Updating UI with " + filteredPets.size() + " pets");

            if (filteredPets.isEmpty()) {
                Log.d(TAG, "📭 No pets to display - showing empty state");
                viewPager2.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
                resultsCounter.setText("0 animaux");
            } else {
                Log.d(TAG, "✅ Displaying " + filteredPets.size() + " pets");
                viewPager2.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);

                adapter = new PetPagerAdapter(MainActivity.this, filteredPets, viewPager2);
                viewPager2.setAdapter(adapter);

                String resultText = filteredPets.size() + " " +
                        (filteredPets.size() > 1 ? "animaux" : "animal");
                resultsCounter.setText(resultText);

                for (int i = 0; i < filteredPets.size(); i++) {
                    Pet pet = filteredPets.get(i);
                    Log.d(TAG, "  [" + i + "] " + pet.getName() + " (" + pet.getBreed() + ") - " + pet.getLocation());
                }
            }
        });
    }

    private void loadPetsFromFirebase() {
        PetRepository repo = new PetRepository();
        Log.d(TAG, "📡 Loading pets from Firestore...");

        repo.loadPets(new PetRepository.PetCallback() {
            @Override
            public void onPetsLoaded(List<Pet> pets) {
                Log.d(TAG, "✅ Pets loaded from Firebase: " + pets.size() + " pets");

                if (pets.isEmpty()) {
                    Log.w(TAG, "⚠️ No pets found in Firebase!");
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this,
                                "❌ Aucun pet trouvé dans Firebase!",
                                Toast.LENGTH_LONG).show();
                        emptyStateLayout.setVisibility(View.VISIBLE);
                    });
                    return;
                }

                allPets.clear();
                allPets.addAll(pets);
                filteredPets.clear();
                filteredPets.addAll(pets);

                Log.d(TAG, "📋 All pets loaded:");
                for (Pet pet : pets) {
                    Log.d(TAG, "  🐾 " + pet.getName() +
                            " | Breed: '" + pet.getBreed() + "'" +
                            " | Age: " + pet.getAge() +
                            " | Location: " + pet.getLocation());
                }

                runOnUiThread(() -> {
                    updateUI();
                    Toast.makeText(MainActivity.this,
                            "✅ " + pets.size() + " animaux chargés!",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Firebase error: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "❌ Erreur Firebase: " + error,
                            Toast.LENGTH_LONG).show();
                    emptyStateLayout.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void configureFirestore() {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build();

            firestore.setFirestoreSettings(settings);
            Log.d(TAG, "✅ Firestore configured");
        } catch (Exception e) {
            Log.e(TAG, "❌ Firestore config error: " + e.getMessage());
        }
    }

    /**
     * ⚡ PageTransformer ultra-rapide et fluide
     */
    private static class FastPageTransformer implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            // Position: -1 (gauche) à 0 (centre) à 1 (droite)

            if (position < -1 || position > 1) {
                // Page hors écran
                page.setAlpha(0f);
            } else {
                // ⚡ Transition simple et rapide (fade + légère échelle)
                float absPosition = Math.abs(position);

                // Fade rapide
                page.setAlpha(1f - absPosition * 0.5f);

                // Échelle subtile (95% à 100%)
                float scale = 0.95f + (1f - absPosition) * 0.05f;
                page.setScaleX(scale);
                page.setScaleY(scale);
            }
        }
    }
}