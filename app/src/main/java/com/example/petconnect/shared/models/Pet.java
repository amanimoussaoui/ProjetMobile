package com.example.petconnect.shared.models;

import com.google.firebase.Timestamp;

public class Pet {
    public String id;
    public String name;
    public String breed;
    public String type; // dog, cat, rabbit, etc.
    public String age;
    public String description;
    public String imageUrl;
    public String shelterLocation;
    public boolean isAvailable;
    public Timestamp createdAt;
    
    public Pet() {
        // Constructor vide pour Firestore
    }
    
    public Pet(String name, String breed, String type, String age,
               String description, String imageUrl, String shelterLocation) {
        this.name = name;
        this.breed = breed;
        this.type = type;
        this.age = age;
        this.description = description;
        this.imageUrl = imageUrl;
        this.shelterLocation = shelterLocation;
        this.isAvailable = true;
        this.createdAt = Timestamp.now();
    }
}
