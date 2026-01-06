package com.example.petconnect.modules.shop.services;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class WishlistManager {
    private static final String TAG = "WishlistManager";
    private FirebaseFirestore db;

    public WishlistManager() {
        db = FirebaseFirestore.getInstance();
    }

    public interface WishlistListener {
        void onSuccess();
        void onError(String error);
    }

    public void addToWishlist(String userId, String productId, WishlistListener listener) {
        Map<String, Object> wishlistItem = new HashMap<>();
        wishlistItem.put("productId", productId);
        wishlistItem.put("addedAt", System.currentTimeMillis());

        db.collection("users").document(userId)
                .collection("wishlist")
                .document(productId)
                .set(wishlistItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Produit ajouté à la wishlist: " + productId);
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur ajout wishlist: " + e.getMessage());
                    if (listener != null) listener.onError(e.getMessage());
                });
    }

    public void removeFromWishlist(String userId, String productId, WishlistListener listener) {
        db.collection("users").document(userId)
                .collection("wishlist")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Produit retiré de la wishlist: " + productId);
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur suppression wishlist: " + e.getMessage());
                    if (listener != null) listener.onError(e.getMessage());
                });
    }
}
