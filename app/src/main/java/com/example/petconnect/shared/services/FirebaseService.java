package com.example.petconnect.shared.services;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseService {
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;
    private static FirebaseAuth auth;
    
    public static void initialize() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Configuration Firestore
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();
        db.setFirestoreSettings(settings);
    }
    
    public static FirebaseFirestore getDb() {
        if (db == null) {
            initialize();
        }
        return db;
    }
    
    public static FirebaseStorage getStorage() {
        if (storage == null) {
            initialize();
        }
        return storage;
    }
    
    public static FirebaseAuth getAuth() {
        if (auth == null) {
            initialize();
        }
        return auth;
    }
}
