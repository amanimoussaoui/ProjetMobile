package com.petconnect.services;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {

    private static final String TAG = "NotificationService";

    // CORRECTION: Dans Firebase Messaging moderne, on utilise RemoteMessage
    // Les classes Message et Notification n'existent plus dans ce contexte

    // Méthode pour envoyer une notification de baisse de prix
    public void sendPriceDropNotification(String userFcmToken, String productId,
                                          String productName, double oldPrice, double newPrice) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "price_drop");
            data.put("productId", productId);
            data.put("oldPrice", String.valueOf(oldPrice));
            data.put("newPrice", String.valueOf(newPrice));

            // Calculer le pourcentage de réduction
            double discountPercent = ((oldPrice - newPrice) / oldPrice) * 100;
            data.put("discountPercent", String.format("%.0f", discountPercent));

            // CORRECTION: Utilisez RemoteMessage.Builder
            RemoteMessage message = new RemoteMessage.Builder(userFcmToken)
                    .setMessageId(generateMessageId())
                    .addData("title", "Prix réduit! 🎉")
                    .addData("body", "Le prix de " + productName + " a baissé de " +
                            String.format("%.0f", discountPercent) + "%!")
                    .addData("click_action", "PRICE_DROP_ACTION")
                    .setData(data)
                    .build();

            // Envoyer via FirebaseMessaging
            FirebaseMessaging.getInstance().send(message);

            Log.d(TAG, "Notification de baisse de prix envoyée pour: " + productName);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }

    // Méthode pour envoyer une notification de réapprovisionnement
    public void sendRestockNotification(String userFcmToken, String productId, String productName) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "restock");
            data.put("productId", productId);

            RemoteMessage message = new RemoteMessage.Builder(userFcmToken)
                    .setMessageId(generateMessageId())
                    .addData("title", "Produit de retour! 📦")
                    .addData("body", productName + " est à nouveau disponible!")
                    .addData("click_action", "RESTOCK_ACTION")
                    .setData(data)
                    .build();

            FirebaseMessaging.getInstance().send(message);

            Log.d(TAG, "Notification de réapprovisionnement envoyée pour: " + productName);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }

    // Méthode pour envoyer une notification de commande
    public void sendOrderNotification(String userFcmToken, String orderNumber, String status) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "order_update");
            data.put("orderNumber", orderNumber);
            data.put("status", status);

            String title = "";
            String body = "";

            switch (status) {
                case "pending":
                    title = "Commande confirmée! ✅";
                    body = "Votre commande " + orderNumber + " a été confirmée.";
                    break;
                case "shipped":
                    title = "Commande expédiée! 🚚";
                    body = "Votre commande " + orderNumber + " a été expédiée.";
                    break;
                case "delivered":
                    title = "Commande livrée! 🎁";
                    body = "Votre commande " + orderNumber + " a été livrée.";
                    break;
                case "cancelled":
                    title = "Commande annulée ❌";
                    body = "Votre commande " + orderNumber + " a été annulée.";
                    break;
                default:
                    title = "Mise à jour de commande";
                    body = "Statut de votre commande " + orderNumber + " mis à jour.";
            }

            RemoteMessage message = new RemoteMessage.Builder(userFcmToken)
                    .setMessageId(generateMessageId())
                    .addData("title", title)
                    .addData("body", body)
                    .addData("click_action", "ORDER_UPDATE_ACTION")
                    .setData(data)
                    .build();

            FirebaseMessaging.getInstance().send(message);

            Log.d(TAG, "Notification de commande envoyée: " + orderNumber + " - " + status);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }

    // Méthode pour envoyer une notification promotionnelle
    public void sendPromotionNotification(String userFcmToken, String promotionTitle,
                                          String promotionDescription) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "promotion");
            data.put("promotionTitle", promotionTitle);

            RemoteMessage message = new RemoteMessage.Builder(userFcmToken)
                    .setMessageId(generateMessageId())
                    .addData("title", "Nouvelle promotion! 🏷️")
                    .addData("body", promotionTitle + ": " + promotionDescription)
                    .addData("click_action", "PROMOTION_ACTION")
                    .setData(data)
                    .build();

            FirebaseMessaging.getInstance().send(message);

            Log.d(TAG, "Notification promotionnelle envoyée: " + promotionTitle);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }

    // Méthode pour envoyer une notification personnalisée
    public void sendCustomNotification(String userFcmToken, String title,
                                       String body, Map<String, String> customData) {
        try {
            RemoteMessage message = new RemoteMessage.Builder(userFcmToken)
                    .setMessageId(generateMessageId())
                    .addData("title", title)
                    .addData("body", body)
                    .addData("click_action", "CUSTOM_ACTION")
                    .setData(customData)
                    .build();

            FirebaseMessaging.getInstance().send(message);

            Log.d(TAG, "Notification personnalisée envoyée: " + title);

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }

    // Méthode utilitaire pour générer un ID de message unique
    private String generateMessageId() {
        return "msg_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // Nouvelle méthode pour envoyer à plusieurs tokens
    public void sendToMultipleUsers(List<String> tokens, String title, String body,
                                    Map<String, String> data) {
        for (String token : tokens) {
            sendCustomNotification(token, title, body, data);
        }
    }
}