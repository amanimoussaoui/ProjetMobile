package com.example.petconnect_event.event.model;

import java.util.Date;

public class EventUpdateLog {
    private String id;
    private String eventId;
    private String changeType; // LOCATION_CHANGED, DATE_CHANGED, etc.
    private String oldValue;
    private String newValue;
    private Date changedAt;
    private String organizerName;

    public EventUpdateLog() {}

    public EventUpdateLog(String eventId, String changeType, String oldValue, String newValue, String organizerName) {
        this.eventId = eventId;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = new Date();
        this.organizerName = organizerName;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public Date getChangedAt() { return changedAt; }
    public void setChangedAt(Date changedAt) { this.changedAt = changedAt; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public String getFormattedDate() {
        if (changedAt == null) return "Date inconnue";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.FRENCH);
        return sdf.format(changedAt);
    }
}
