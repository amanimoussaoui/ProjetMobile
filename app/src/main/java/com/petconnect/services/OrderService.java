package com.petconnect.services;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.petconnect.models.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private static final String TAG = "OrderService";
    private FirebaseFirestore db;
    private String userId;

    public OrderService(String userId) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId != null ? userId : "local_user";
    }

    // Interface pour les callbacks
    public interface OrderListener {
        void onSuccess(String orderId);
        void onError(String error);
    }

    public interface OrderStatusListener {
        void onStatusChecked(boolean isPaid);
        void onError(String error);
    }

    public interface OrderRetrieveListener {
        void onOrderRetrieved(Order order);
        void onError(String error);
    }

    public interface OrderListListener {
        void onOrdersLoaded(List<Order> orders);
        void onError(String error);
    }

    // Passer une commande
    public void placeOrder(Order order, OrderListener listener) {
        if (order == null) {
            Log.e(TAG, "Order est null dans placeOrder()");
            if (listener != null) listener.onError("Commande invalide");
            return;
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            Log.e(TAG, "Order sans items dans placeOrder()");
            if (listener != null) listener.onError("Commande sans produits");
            return;
        }

        // S'assurer que les documents parent existent
        ensureParentDocuments();

        // Convertir les items avec toutes les informations
        List<Map<String, Object>> itemsList = convertItemsToMap(order.getItems());

        if (itemsList.isEmpty()) {
            Log.e(TAG, "Aucun item valide dans la commande");
            if (listener != null) listener.onError("Aucun produit valide dans la commande");
            return;
        }

        // S'assurer que le orderNumber est valide
        String orderNumber = order.getOrderNumber();
        if (orderNumber == null || orderNumber.isEmpty()) {
            orderNumber = "ORD_" + System.currentTimeMillis();
            order.setOrderNumber(orderNumber);
        }

        // Créer les données de la commande
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderNumber", orderNumber);
        orderData.put("userId", userId);
        orderData.put("items", itemsList);
        orderData.put("total", order.getTotal());
        orderData.put("status", order.getStatus() != null ? order.getStatus() : "pending");
        orderData.put("paymentId", order.getPaymentId() != null ? order.getPaymentId() : "");
        orderData.put("isPaid", order.isPaid());
        orderData.put("createdAt", FieldValue.serverTimestamp());
        orderData.put("updatedAt", FieldValue.serverTimestamp());
        orderData.put("deliveryAddress", order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "");
        orderData.put("customerName", order.getCustomerName() != null ? order.getCustomerName() : "");
        orderData.put("customerEmail", order.getCustomerEmail() != null ? order.getCustomerEmail() : "");
        orderData.put("customerPhone", order.getCustomerPhone() != null ? order.getCustomerPhone() : "");

        String orderId = orderNumber;

        Log.d(TAG, "💾 Sauvegarde commande: " + orderId + " avec " + itemsList.size() + " items");

        // Sauvegarder dans PetConnect/orders/all_orders
        db.collection("PetConnect")
                .document("orders")
                .collection("all_orders")
                .document(orderId)
                .set(orderData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Commande sauvegardée dans PetConnect/orders/all_orders: " + orderId);
                    Log.d(TAG, "   Chemin: PetConnect/orders/all_orders/" + orderId);
                    Log.d(TAG, "   Items: " + itemsList.size());

                    // Sauvegarder aussi dans la collection utilisateur pour historique
                    saveToUserOrders(orderId, orderData, listener);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur sauvegarde commande principale: " + e.getMessage());
                    Log.e(TAG, "   Chemin: PetConnect/orders/all_orders/" + orderId);
                    // Essayer de sauvegarder uniquement chez l'utilisateur
                    saveToUserOrders(orderId, orderData, listener);
                });
    }

    /**
     * Sauvegarder la commande dans la collection utilisateur
     */
    private void saveToUserOrders(String orderId, Map<String, Object> orderData, OrderListener listener) {
        db.collection("users")
                .document(userId)
                .collection("orders")
                .document(orderId)
                .set(orderData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Commande sauvegardée dans users/" + userId + "/orders: " + orderId);
                    if (listener != null) listener.onSuccess(orderId);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "⚠️ Erreur sauvegarde commande utilisateur: " + e.getMessage());
                    // Ne pas bloquer si cette sauvegarde échoue
                    if (listener != null) listener.onSuccess(orderId);
                });
    }

    /**
     * Créer les documents parent PetConnect et orders dans Firebase si nécessaire
     */
    private void ensureParentDocuments() {
        // Créer le document parent "PetConnect" s'il n'existe pas
        Map<String, Object> petConnectData = new HashMap<>();
        petConnectData.put("appName", "PetConnect");
        petConnectData.put("lastUpdated", FieldValue.serverTimestamp());
        petConnectData.put("version", "1.0");
        db.collection("PetConnect")
                .document("_info")
                .set(petConnectData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "✅ Document PetConnect créé/mis à jour"))
                .addOnFailureListener(e -> Log.w(TAG, "⚠️ Erreur création document PetConnect: " + e.getMessage()));

        // Créer le document parent "orders" s'il n'existe pas
        Map<String, Object> ordersData = new HashMap<>();
        ordersData.put("ordersName", "Orders");
        ordersData.put("lastUpdated", FieldValue.serverTimestamp());
        db.collection("PetConnect")
                .document("orders")
                .set(ordersData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "✅ Document Orders créé/mis à jour"))
                .addOnFailureListener(e -> Log.w(TAG, "⚠️ Erreur création document Orders: " + e.getMessage()));
    }

    /**
     * Méthode utilitaire pour convertir les items avec toutes les informations (CORRIGÉE)
     */
    private List<Map<String, Object>> convertItemsToMap(List<CartService.CartItem> items) {
        List<Map<String, Object>> itemsList = new ArrayList<>();
        if (items != null) {
            for (CartService.CartItem item : items) {
                if (item == null || item.product == null) {
                    Log.w(TAG, "⚠️ Item ou produit null ignoré lors de la conversion");
                    continue;
                }

                Map<String, Object> itemMap = new HashMap<>();

                // Informations de base du produit
                itemMap.put("productId", item.product.getId() != null ? item.product.getId() : "");
                itemMap.put("productName", item.product.getName() != null ? item.product.getName() : "");
                itemMap.put("quantity", item.quantity); // CORRECTION: pas de vérification null pour int
                itemMap.put("price", item.product.getPrice());
                itemMap.put("itemTotal", item.getItemTotal());

                // Informations supplémentaires du produit
                String imageUrl = String.valueOf(item.product.getImageUrl());
                itemMap.put("imageUrl", imageUrl != null ? imageUrl : "");

                String category = item.product.getCategory();
                itemMap.put("category", category != null ? category : "");

                String description = item.product.getDescription();
                itemMap.put("description", description != null ? description : "");

                itemsList.add(itemMap);
                Log.d(TAG, "📦 Item converti: " + item.product.getName() + " x" + item.quantity);
            }
        }

        Log.d(TAG, "📊 Total items convertis: " + itemsList.size());
        return itemsList;
    }

    // Vérifier le statut de paiement
    public void checkPaymentStatus(String orderId, OrderStatusListener listener) {
        if (orderId == null || orderId.isEmpty()) {
            if (listener != null) listener.onError("ID commande invalide");
            return;
        }

        // Chercher d'abord dans PetConnect/orders/all_orders
        db.collection("PetConnect")
                .document("orders")
                .collection("all_orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isPaid = documentSnapshot.getBoolean("isPaid");
                        if (listener != null) {
                            listener.onStatusChecked(isPaid != null && isPaid);
                        }
                    } else {
                        // Chercher dans la collection utilisateur
                        db.collection("users")
                                .document(userId)
                                .collection("orders")
                                .document(orderId)
                                .get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        Boolean isPaid = doc.getBoolean("isPaid");
                                        if (listener != null) {
                                            listener.onStatusChecked(isPaid != null && isPaid);
                                        }
                                    } else {
                                        if (listener != null) listener.onError("Commande non trouvée");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (listener != null) listener.onError(e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onError(e.getMessage());
                });
    }

    // Obtenir une commande par ID
    public void getOrderById(String orderId, OrderRetrieveListener listener) {
        if (orderId == null || orderId.isEmpty()) {
            if (listener != null) listener.onError("ID commande invalide");
            return;
        }

        // Chercher d'abord dans PetConnect/orders/all_orders
        db.collection("PetConnect")
                .document("orders")
                .collection("all_orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Order order = convertToOrder(documentSnapshot);
                        if (order != null) {
                            if (listener != null) listener.onOrderRetrieved(order);
                        } else {
                            if (listener != null) listener.onError("Erreur conversion commande");
                        }
                    } else {
                        // Chercher dans la collection utilisateur
                        db.collection("users")
                                .document(userId)
                                .collection("orders")
                                .document(orderId)
                                .get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        Order order = convertToOrder(doc);
                                        if (order != null) {
                                            if (listener != null) listener.onOrderRetrieved(order);
                                        } else {
                                            if (listener != null) listener.onError("Erreur conversion commande");
                                        }
                                    } else {
                                        if (listener != null) listener.onError("Commande non trouvée");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (listener != null) listener.onError(e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onError(e.getMessage());
                });
    }

    /**
     * Convertir DocumentSnapshot en objet Order
     */
    private Order convertToOrder(DocumentSnapshot documentSnapshot) {
        try {
            String orderId = documentSnapshot.getId();
            String orderNumber = documentSnapshot.getString("orderNumber");
            String userId = documentSnapshot.getString("userId");

            // Créer une commande vide avec l'ID
            Order order = new Order();
            order.setId(orderId);
            order.setOrderNumber(orderNumber != null ? orderNumber : orderId);
            order.setUserId(userId != null ? userId : this.userId);

            // Récupérer les valeurs de base
            Double total = documentSnapshot.getDouble("total");
            if (total != null) {
                order.setTotal(total);
            }

            String status = documentSnapshot.getString("status");
            order.setStatus(status != null ? status : "pending");

            String paymentId = documentSnapshot.getString("paymentId");
            order.setPaymentId(paymentId);

            Boolean isPaid = documentSnapshot.getBoolean("isPaid");
            order.setPaid(isPaid != null && isPaid);

            // Récupérer les items (liste de Map)
            Object itemsObj = documentSnapshot.get("items");
            if (itemsObj instanceof List) {
                List<CartService.CartItem> cartItems = new ArrayList<>();
                List<Map<String, Object>> itemsList = (List<Map<String, Object>>) itemsObj;

                for (Map<String, Object> itemMap : itemsList) {
                    CartService.CartItem cartItem = CartService.CartItem.fromMap(itemMap);
                    if (cartItem != null) {
                        cartItems.add(cartItem);
                    }
                }

                order.setItems(cartItems);
            }

            // Informations client
            order.setDeliveryAddress(documentSnapshot.getString("deliveryAddress"));
            order.setCustomerName(documentSnapshot.getString("customerName"));
            order.setCustomerEmail(documentSnapshot.getString("customerEmail"));
            order.setCustomerPhone(documentSnapshot.getString("customerPhone"));

            // Timestamps
            com.google.firebase.Timestamp createdAt = documentSnapshot.getTimestamp("createdAt");
            if (createdAt != null) {
                order.setCreatedAt(createdAt.toDate());
            }

            return order;

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur conversion DocumentSnapshot en Order: " + e.getMessage());
            return null;
        }
    }

    // Obtenir toutes les commandes de l'utilisateur
    public void getUserOrders(OrderListListener listener) {
        // Chercher dans PetConnect/orders/all_orders
        db.collection("PetConnect")
                .document("orders")
                .collection("all_orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = convertToOrder(doc);
                        if (order != null) {
                            orders.add(order);
                        }
                    }

                    Log.d(TAG, "📋 Commandes trouvées dans PetConnect: " + orders.size());

                    // Si aucune commande, chercher dans la collection utilisateur
                    if (orders.isEmpty()) {
                        db.collection("users")
                                .document(userId)
                                .collection("orders")
                                .get()
                                .addOnSuccessListener(userQuery -> {
                                    List<Order> userOrders = new ArrayList<>();

                                    for (DocumentSnapshot doc : userQuery) {
                                        Order order = convertToOrder(doc);
                                        if (order != null) {
                                            userOrders.add(order);
                                        }
                                    }

                                    Log.d(TAG, "📋 Commandes trouvées dans users: " + userOrders.size());
                                    if (listener != null) listener.onOrdersLoaded(userOrders);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "❌ Erreur chargement commandes utilisateur: " + e.getMessage());
                                    if (listener != null) listener.onError(e.getMessage());
                                });
                    } else {
                        if (listener != null) listener.onOrdersLoaded(orders);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur chargement commandes: " + e.getMessage());
                    if (listener != null) listener.onError(e.getMessage());
                });
    }

    // Mettre à jour le statut d'une commande
    public void updateOrderStatus(String orderId, String status, boolean isPaid, OrderListener listener) {
        if (orderId == null || orderId.isEmpty()) {
            if (listener != null) listener.onError("ID commande invalide");
            return;
        }

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", status != null ? status : "pending");
        updateData.put("isPaid", isPaid);
        updateData.put("updatedAt", FieldValue.serverTimestamp());

        // Mettre à jour dans PetConnect/orders/all_orders
        db.collection("PetConnect")
                .document("orders")
                .collection("all_orders")
                .document(orderId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Statut commande mis à jour dans PetConnect: " + orderId + " -> " + status);

                    // Mettre à jour aussi dans la collection utilisateur
                    db.collection("users")
                            .document(userId)
                            .collection("orders")
                            .document(orderId)
                            .update(updateData)
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d(TAG, "✅ Statut commande mis à jour dans users: " + orderId);
                                if (listener != null) listener.onSuccess(orderId);
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "⚠️ Erreur mise à jour statut users (non bloquant): " + e.getMessage());
                                if (listener != null) listener.onSuccess(orderId);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur mise à jour statut: " + e.getMessage());
                    if (listener != null) listener.onError(e.getMessage());
                });
    }
}