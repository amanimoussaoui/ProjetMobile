package com.petconnect.models;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private double price;
    private int imageRes; // image locale (R.drawable...)
    private String imageUrl; // URL Firebase Storage
    private String description;
    private String category;
    private int stockQuantity;
    private double rating;
    private boolean isAvailable;

    public Product(String id, String name, double price, int imageRes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.isAvailable = true;
    }

    // Constructeur vide (obligatoire)
    public Product() {
        this.isAvailable = true;
    }

    // =========================
    // GETTERS
    // =========================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getFinalPrice() {
        return price;
    }

    public double getPrice() {
        return price;
    }

    public int getImageRes() {
        return imageRes;
    }

    // Compatibilité ancienne - pour image locale
    public int getImageUrl() {
        return imageRes;
    }

    // Nouvelle méthode pour obtenir l'URL Firebase
    public String getImageUrlString() {
        return imageUrl;
    }

    // =========================
    // SETTERS
    // =========================

    public void setId(String productId) {
        this.id = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Définir l'URL de l'image depuis Firebase
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    // =========================
    // MÉTHODES UTILISÉES PAR LE DIALOG
    // =========================

    public boolean hasDiscount() {
        return false; // pas de promo
    }

    public double getDiscountPrice() {
        return price; // même prix
    }

    public String getDescription() {
        return description != null ? description : "Produit de qualité pour votre animal.";
    }

    public String getCategory() {
        return category != null ? category : "Animalerie";
    }

    public int getStockQuantity() {
        return stockQuantity > 0 ? stockQuantity : 10;
    }

    public double getRating() {
        return rating > 0 ? rating : 4.5;
    }

    public String getMainImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}