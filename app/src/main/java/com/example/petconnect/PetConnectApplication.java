package com.example.petconnect;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class PetConnectApplication extends Application {
    private static final String TAG = "PetConnectApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Le plugin Google Services initialise Firebase automatiquement via google-services.json
        // Vérifier que Firebase est bien initialisé
        try {
            FirebaseApp firebaseApp = FirebaseApp.getInstance();
            Log.d(TAG, "Firebase initialized successfully: " + firebaseApp.getName());
            
            // Configurer Firestore (optionnel)
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true) // Activer le cache local
                    .build();
            firestore.setFirestoreSettings(settings);
            Log.d(TAG, "Firestore configured successfully");
        } catch (IllegalStateException e) {
            // Si Firebase n'est pas initialisé, l'initialiser manuellement
            Log.w(TAG, "Firebase not initialized, initializing now...", e);
            try {
                FirebaseApp.initializeApp(this);
                Log.d(TAG, "Firebase initialized manually");
            } catch (Exception ex) {
                Log.e(TAG, "Error initializing Firebase", ex);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configuring Firebase", e);
        }
    }
}

