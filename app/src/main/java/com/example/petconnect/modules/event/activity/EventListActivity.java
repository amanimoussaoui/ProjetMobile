package com.example.petconnect.modules.event.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.petconnect.R;
import com.example.petconnect.modules.event.adapter.EventCardModernAdapter;
import com.example.petconnect.modules.event.model.Event;
import com.example.petconnect.modules.event.repository.EventRepository;
import com.example.petconnect.modules.event.util.AnimationManager;
import com.example.petconnect.modules.event.util.FavoritesManager;
import com.example.petconnect.modules.event.util.HapticFeedbackManager;
import com.example.petconnect.modules.event.util.QRCodeGenerator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventListActivity extends AppCompatActivity implements EventCardModernAdapter.OnEventActionListener {

    private static final String TAG = "EventListActivity";

    private RecyclerView recyclerView;
    private EventCardModernAdapter eventAdapter;
    private EventRepository eventRepository;
    private FavoritesManager favoritesManager;
    private FloatingActionButton fabAddEvent;
    private TextInputEditText searchEditText;
    private Chip chipUpcoming, chipFavorites, chipOpen;
    private BottomNavigationView bottomNavigation;
    private ShimmerFrameLayout shimmerLoading;
    private View emptyStateContainer;
    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    
    private boolean showOnlyFavorites = false;
    private boolean showOnlyUpcoming = true;
    private boolean showOnlyAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_new);

        initViews();
        initRepository();
        setupRecyclerView();
        setupListeners();
        setupBottomNavigation();
        loadEvents();
    }

    private void initViews() {
        try {
            recyclerView = findViewById(R.id.recyclerViewEvents);
            fabAddEvent = findViewById(R.id.fabCreateEvent);
            searchEditText = findViewById(R.id.editTextSearch);
            chipUpcoming = findViewById(R.id.chipUpcoming);
            chipFavorites = findViewById(R.id.chipFavorites);
            chipOpen = findViewById(R.id.chipOpen);
            bottomNavigation = findViewById(R.id.bottomNavigation);
            shimmerLoading = findViewById(R.id.shimmerLoading);
            emptyStateContainer = findViewById(R.id.emptyStateContainer);
            
            // Verify all views are found
            if (recyclerView == null || fabAddEvent == null || searchEditText == null ||
                chipUpcoming == null || chipFavorites == null || chipOpen == null ||
                bottomNavigation == null || emptyStateContainer == null) {
                Log.e(TAG, "One or more views not found in layout!");
                Toast.makeText(this, "Erreur: Vue manquante dans le layout", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in initViews: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur initialisation des vues: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initRepository() {
        eventRepository = new EventRepository();
        favoritesManager = new FavoritesManager(this);
    }

    private void setupRecyclerView() {
        eventAdapter = new EventCardModernAdapter(filteredEvents, favoritesManager);
        eventAdapter.setOnEventActionListener(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventAdapter);
        
        recyclerView.setHasFixedSize(true);
    }

    private void setupListeners() {
        // FAB - Créer événement
        fabAddEvent.setOnClickListener(v -> {
            HapticFeedbackManager.vibrateClick(v);
            Intent intent = new Intent(EventListActivity.this, AddEditEventActivity.class);
            startActivity(intent);
            AnimationManager.startActivityWithAnimation(EventListActivity.this, null);
        });

        // Recherche
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Chips filtres
        chipUpcoming.setOnClickListener(v -> {
            HapticFeedbackManager.vibrateClick(v);
            showOnlyUpcoming = chipUpcoming.isChecked();
            filterEvents();
        });

        chipFavorites.setOnClickListener(v -> {
            HapticFeedbackManager.vibrateClick(v);
            showOnlyFavorites = chipFavorites.isChecked();
            filterEvents();
        });

        chipOpen.setOnClickListener(v -> {
            HapticFeedbackManager.vibrateClick(v);
            showOnlyAvailable = chipOpen.isChecked();
            filterEvents();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            HapticFeedbackManager.vibrateClick(bottomNavigation);
            
            if (id == R.id.nav_home) {
                // Déjà ici
                return true;
            } else if (id == R.id.nav_search) {
                // TODO: Open search advanced screen
                Toast.makeText(this, "Recherche avancée", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_favorites) {
                chipFavorites.setChecked(!chipFavorites.isChecked());
                showOnlyFavorites = chipFavorites.isChecked();
                filterEvents();
                return true;
            } else if (id == R.id.nav_profile) {
                // TODO: Open profile screen
                Toast.makeText(this, "Profil utilisateur", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadEvents() {
        showShimmer(true);
        
        eventRepository.getAllEvents(new EventRepository.OnEventsLoadedListener() {
            @Override
            public void onLoaded(List<Event> events) {
                allEvents = events;
                filterEvents();
                showShimmer(false);
            }
        });
    }

    private void filterEvents() {
        filteredEvents.clear();
        String searchText = searchEditText.getText().toString().toLowerCase().trim();
        
        for (Event event : allEvents) {
            boolean matches = true;

            // Filtre texte
            if (!searchText.isEmpty()) {
                matches = event.getTitle().toLowerCase().contains(searchText) ||
                         event.getDescription().toLowerCase().contains(searchText) ||
                         event.getLocation().toLowerCase().contains(searchText);
            }

            // Filtre à venir
            if (showOnlyUpcoming && matches) {
                if (event.getEventDate() != null) {
                    matches = event.getEventDate().after(new Date());
                }
            }

            // Filtre favoris
            if (showOnlyFavorites && matches) {
                matches = favoritesManager.isFavorite(event.getId());
            }

            // Filtre places disponibles
            if (showOnlyAvailable && matches) {
                matches = event.getCurrentParticipants() < event.getMaxParticipants();
            }

            if (matches) {
                filteredEvents.add(event);
            }
        }

        eventAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredEvents.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        }
    }

    private void showShimmer(boolean show) {
        if (shimmerLoading == null) {
            Log.w(TAG, "shimmerLoading view is null, skipping shimmer");
            return;
        }
        
        try {
            if (show) {
                shimmerLoading.setVisibility(View.VISIBLE);
                shimmerLoading.startShimmer();
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
            } else {
                shimmerLoading.setVisibility(View.GONE);
                shimmerLoading.stopShimmer();
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing shimmer: " + e.getMessage(), e);
        }
    }

    @Override
    public void onView(Event event) {
        Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
        intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.getId());
        startActivity(intent);
        AnimationManager.startActivityWithAnimation(EventListActivity.this, null);
    }

    @Override
    public void onShare(Event event) {
        try {
            // Générer QR code
            Bitmap qrBitmap = QRCodeGenerator.generateQRCode(event.getId(), 512, 512);
            
            // Sauvegarder et partager
            Uri qrUri = QRCodeGenerator.saveBitmapToCache(this, qrBitmap, "event_qr.png");
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, qrUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Rejoignez mon événement: " + event.getTitle());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            startActivity(Intent.createChooser(shareIntent, "Partager l'événement"));
        } catch (Exception e) {
            Toast.makeText(this, "Erreur partage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteToggle(Event event) {
        HapticFeedbackManager.vibrateSuccess(recyclerView);
        
        if (favoritesManager.isFavorite(event.getId())) {
            favoritesManager.removeFavorite(event.getId());
            Toast.makeText(this, "Retiré des favoris", Toast.LENGTH_SHORT).show();
        } else {
            favoritesManager.addFavorite(event.getId());
            Toast.makeText(this, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
        }
        
        eventAdapter.notifyDataSetChanged();
        
        // Refilter si nécessaire
        if (showOnlyFavorites) {
            filterEvents();
        }
    }

    @Override
    public void onBackPressed() {
        AnimationManager.finishActivityWithAnimation(this);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }
}
