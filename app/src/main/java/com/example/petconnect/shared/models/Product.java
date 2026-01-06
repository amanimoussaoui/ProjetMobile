package com.example.petconnect.shared.models;

import com.google.firebase.Timestamp;

public class Product {
    public String id;
    public String name;
    public String description;
    public double price;
    public String category; // food, toys, accessories, etc.
    public String imageUrl;
    public int stock;
    public double rating;
    public Timestamp createdAt;
    
    public Product() {
        // Constructor vide pour Firestore
    }
    
    public Product(String name, String description, double price,
                   String category, String imageUrl, int stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.rating = 0.0;
        this.createdAt = Timestamp.now();
    }
}
