package com.example.petconnect;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.petconnect.shared.services.FirebaseService;
import com.example.petconnect.ui.fragments.AdoptionFragment;
import com.example.petconnect.ui.fragments.EventsFragment;
import com.example.petconnect.ui.fragments.HomeFragment;
import com.example.petconnect.ui.fragments.ShopFragment;
import com.example.petconnect.modules.event.activity.EventListActivity;
import com.example.petconnect.modules.user.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialiser Firebase
        FirebaseService.initialize();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_nav);
        
        // Charger le fragment Home par défaut
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
        
        // Gestion de la navigation
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            Intent intent = null;
            boolean useFragment = true;
            
            if (item.getItemId() == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_events) {
                // Option 1 : Lance l'activité Event complète
                intent = new Intent(MainActivity.this, EventListActivity.class);
                useFragment = false;
            } else if (item.getItemId() == R.id.nav_adoption) {
                // Option 2 : Lance l'activité Adoption complète
                intent = new Intent(MainActivity.this, com.example.petconnect.modules.adoption.MainActivity.class);
                useFragment = false;
            } else if (item.getItemId() == R.id.nav_shop) {
                // Option 3 : Lance l'activité Shop complète
                intent = new Intent(MainActivity.this, com.example.petconnect.modules.shop.MainActivity.class);
                useFragment = false;
            } else if (item.getItemId() == R.id.nav_profile) {
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                useFragment = false;
            }
            
            if (useFragment && fragment != null) {
                loadFragment(fragment);
            } else if (!useFragment && intent != null) {
                startActivity(intent);
            }
            
            return true;
        });
    }
    
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
}