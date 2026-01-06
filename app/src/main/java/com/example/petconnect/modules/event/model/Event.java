package com.example.petconnect.modules.event.model;

import com.google.firebase.firestore.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Event {
    private String id;
    private String title;
    private String description;
    private String location;
    private Date eventDate;
    private String imageUrl;
    private String organizerId;
    private int maxParticipants;
    private int currentParticipants;
    private Date createdAt;
    private boolean isActive;

    // Constructeur vide requis pour Firebase
    public Event() {
    }

    // Constructeur complet
    public Event(String id, String title, String description, String location,
                 Date eventDate, String imageUrl, String organizerId,
                 int maxParticipants, int currentParticipants) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
        this.organizerId = organizerId;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
        this.createdAt = new Date();
        this.isActive = true;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    
    // Setter pour gérer les String de Firestore
    @Exclude
    public void setEventDate(String eventDateString) {
        if (eventDateString == null || eventDateString.isEmpty()) {
            this.eventDate = null;
            return;
        }
        
        try {
            // Format ISO 8601: "2026-02-15T14:00:00Z"
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            this.eventDate = isoFormat.parse(eventDateString);
        } catch (ParseException e) {
            try {
                // Format alternatif: "2026-02-15 14:00:00"
                SimpleDateFormat altFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                this.eventDate = altFormat.parse(eventDateString);
            } catch (ParseException e2) {
                try {
                    // Format court: "2026-02-15"
                    SimpleDateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    this.eventDate = shortFormat.parse(eventDateString);
                } catch (ParseException e3) {
                    // Si aucun format ne fonctionne, garder null
                    this.eventDate = null;
                }
            }
        }
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    // Setter pour gérer les String de Firestore
    @Exclude
    public void setCreatedAt(String createdAtString) {
        if (createdAtString == null || createdAtString.isEmpty()) {
            this.createdAt = null;
            return;
        }
        
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            this.createdAt = isoFormat.parse(createdAtString);
        } catch (ParseException e) {
            this.createdAt = null;
        }
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
