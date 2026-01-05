package com.petconnect.models;

import com.petconnect.services.CartService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String orderNumber;
    private String userId;
    private List<CartService.CartItem> items; // Utilise CartService.CartItem
    private double total;
    private String status; // pending, paid, shipped, delivered, cancelled
    private String paymentId;
    private boolean isPaid;
    private Date createdAt;
    private String deliveryAddress;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Constructeurs
    public Order() {}

    public Order(String orderNumber, String userId, List<CartService.CartItem> items) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.items = items;
        this.total = calculateTotal();
        this.status = "pending";
        this.isPaid = false;
        this.createdAt = new Date();
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartService.CartItem> getItems() { return items; }
    public void setItems(List<CartService.CartItem> items) {
        this.items = items;
        this.total = calculateTotal();
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    // Méthodes utilitaires
    private double calculateTotal() {
        double total = 0;
        if (items != null) {
            for (CartService.CartItem item : items) {
                total += item.getItemTotal();
            }
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        if (items != null) {
            for (CartService.CartItem item : items) {
                count += item.quantity;
            }
        }
        return count;
    }

    // Méthode pour accepter directement les items du service
    public void setServiceItems(List<CartService.CartItem> serviceItems) {
        this.items = new ArrayList<>(serviceItems);
        this.total = calculateTotal();
    }

    // Méthode pour accepter les items du service (alias de setServiceItems)
    public void setServiceCartItems(List<CartService.CartItem> serviceCartItems) {
        this.items = new ArrayList<>(serviceCartItems);
        this.total = calculateTotal();
    }
}