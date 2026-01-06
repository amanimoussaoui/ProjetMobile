package com.example.petconnect.shared.models;

import com.google.firebase.Timestamp;

public class Event {
    public String id;
    public String title;
    public String description;
    public String location;
    public Timestamp eventDate;
    public String imageUrl;
    public String organizerId;
    public int maxParticipants;
    public int currentParticipants;
    public boolean isActive;
    public Timestamp createdAt;
    
    public Event() {
        // Constructor vide pour Firestore
    }
    
    public Event(String title, String description, String location, 
                 Timestamp eventDate, String imageUrl, String organizerId,
                 int maxParticipants) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
        this.organizerId = organizerId;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
        this.isActive = true;
        this.createdAt = Timestamp.now();
    }
}
