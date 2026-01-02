package com.example.petconnect;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pet implements Serializable {
    // Champs avec @PropertyName pour Firestore
    @PropertyName("name")
    private String name = "";

    @PropertyName("age")
    private String age = "";

    @PropertyName("gender")
    private String gender = "";

    @PropertyName("breed")
    private String breed = "";

    @PropertyName("location")
    private String location = "";

    @PropertyName("emoji")
    private String emoji = "";

    @PropertyName("photoPath")
    private String photoPath = "";

    @PropertyName("about")
    private String about = "";

    @PropertyName("requirements")
    private List<String> requirements = new ArrayList<>();

    private int id;

    // ⭐ NOUVEAU : Stocke l'ID du document Firestore
    private String firestoreId;

    // Constructeur vide OBLIGATOIRE pour Firestore
    public Pet() {
        this.requirements = new ArrayList<>();
    }

    // Constructeur avec paramètres
    public Pet(String name, String age, String gender, String breed,
               String location, String emoji, String photoPath,
               String about, List<String> requirements) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.breed = breed;
        this.location = location;
        this.emoji = emoji;
        this.photoPath = photoPath;
        this.about = about;
        this.requirements = requirements != null ? requirements : new ArrayList<>();
    }

    // GETTERS
    @PropertyName("name")
    public String getName() {
        return name != null ? name : "";
    }

    @PropertyName("age")
    public String getAge() {
        return age != null ? age : "";
    }

    @PropertyName("gender")
    public String getGender() {
        return gender != null ? gender : "";
    }

    @PropertyName("breed")
    public String getBreed() {
        return breed != null ? breed : "";
    }

    @PropertyName("location")
    public String getLocation() {
        return location != null ? location : "";
    }

    @PropertyName("emoji")
    public String getEmoji() {
        return emoji != null ? emoji : "🐾";
    }

    @PropertyName("photoPath")
    public String getPhotoPath() {
        return photoPath != null ? photoPath : "";
    }

    @PropertyName("about")
    public String getAbout() {
        return about != null ? about : "";
    }

    @PropertyName("requirements")
    public List<String> getRequirements() {
        return requirements != null ? requirements : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    // ⭐ NOUVEAU : Getter pour l'ID Firestore
    public String getFirestoreId() {
        return firestoreId;
    }

    // SETTERS
    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("age")
    public void setAge(String age) {
        this.age = age;
    }

    @PropertyName("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @PropertyName("breed")
    public void setBreed(String breed) {
        this.breed = breed;
    }

    @PropertyName("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @PropertyName("emoji")
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    @PropertyName("photoPath")
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @PropertyName("about")
    public void setAbout(String about) {
        this.about = about;
    }

    @PropertyName("requirements")
    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public void setId(int id) {
        this.id = id;
    }

    // ⭐ NOUVEAU : Setter pour l'ID Firestore
    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", breed='" + breed + '\'' +
                ", emoji='" + emoji + '\'' +
                ", location='" + location + '\'' +
                ", firestoreId='" + firestoreId + '\'' +
                '}';
    }
}