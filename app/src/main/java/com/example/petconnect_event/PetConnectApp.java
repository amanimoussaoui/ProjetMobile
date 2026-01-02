package com.example.petconnect_event;

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
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage(), e);
        }
    }
}
