package com.example.petconnect;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class PetConnectApp extends Application {
    
    private static final String TAG = "PetConnectApp";

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            Log.d(TAG, "Initializing Firebase...");
            // Initialize Firebase
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                Log.d(TAG, "Firebase initialized successfully");
            } else {
                Log.d(TAG, "Firebase already initialized");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage(), e);
            // Continue anyway - RegisterActivity will show proper error
        }
    }
}
