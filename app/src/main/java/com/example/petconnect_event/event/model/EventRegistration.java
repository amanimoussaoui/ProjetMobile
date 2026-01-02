package com.example.petconnect_event.event.model;

import java.util.Date;

public class EventRegistration {
    private String id;
    private String eventId;
    private String userId;
    private String userName;
    private String userEmail;
    private Date registrationDate;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private String notes;

    // Constructeur vide pour Firebase
    public EventRegistration() {
    }

    // Constructeur complet
    public EventRegistration(String id, String eventId, String userId,
                             String userName, String userEmail) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.registrationDate = new Date();
        this.status = "CONFIRMED";
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}