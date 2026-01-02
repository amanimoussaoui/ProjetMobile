package com.example.petconnect.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String photoUrl;
    private String role; // "user", "admin", "adoption_center"
    private Map<String, Boolean> animalPreferences; // Ex: {"dogs": true, "cats": false}
    private String lifestyle; // "active", "moderate", "calm"
    private String housingType; // "apartment", "house", "farm"
    private boolean hasGarden; // A un jardin ou non
    private String experienceLevel; // "beginner", "intermediate", "expert"
    private String avatarData; // Données JSON pour l'avatar personnalisé (Bitmoji)
    private long createdAt;
    private long updatedAt;

    // Constructeurs
    public User() {
        // Constructeur par défaut requis pour Firestore
        this.animalPreferences = new HashMap<>();
        this.role = "user"; // Par défaut
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public User(String userId, String firstName, String lastName, String email) {
        this();
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters et Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, Boolean> getAnimalPreferences() {
        return animalPreferences;
    }

    public void setAnimalPreferences(Map<String, Boolean> animalPreferences) {
        this.animalPreferences = animalPreferences;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Méthodes utilitaires
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getLifestyle() {
        return lifestyle;
    }

    public void setLifestyle(String lifestyle) {
        this.lifestyle = lifestyle;
    }

    public String getHousingType() {
        return housingType;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    public boolean isHasGarden() {
        return hasGarden;
    }

    public void setHasGarden(boolean hasGarden) {
        this.hasGarden = hasGarden;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getAvatarData() {
        return avatarData;
    }

    public void setAvatarData(String avatarData) {
        this.avatarData = avatarData;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("email", email);
        map.put("phoneNumber", phoneNumber);
        map.put("address", address);
        map.put("photoUrl", photoUrl);
        map.put("role", role);
        map.put("animalPreferences", animalPreferences);
        map.put("lifestyle", lifestyle);
        map.put("housingType", housingType);
        map.put("hasGarden", hasGarden);
        map.put("experienceLevel", experienceLevel);
        map.put("avatarData", avatarData);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }
}



