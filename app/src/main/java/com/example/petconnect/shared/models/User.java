package com.example.petconnect.shared.models;

import com.google.firebase.Timestamp;

public class User {
    public String userId;
    public String name;
    public String email;
    public String phone;
    public String profileImage;
    public String location;
    public Timestamp createdAt;
    
    public User() {
        // Constructor vide pour Firestore
    }
    
    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = Timestamp.now();
    }
}
