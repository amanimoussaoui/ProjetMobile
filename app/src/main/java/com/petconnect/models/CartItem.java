package com.petconnect.models;

import com.petconnect.models.Product;

public class CartItem {
    public Product product;
    public int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public String getProductId() { return product.getId(); }
    public String getProductName() { return product.getName(); }
    public double getPrice() { return product.getPrice(); }
    public int getQuantity() { return quantity; }

    // --- AJOUTER CETTE MÉTHODE ---
    public double getItemTotal() {
        return product.getPrice() * quantity;
    }
}
