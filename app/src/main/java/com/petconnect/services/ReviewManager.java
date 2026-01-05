package com.petconnect.services;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewManager {

    private FirebaseFirestore db;

    public ReviewManager() {
        db = FirebaseFirestore.getInstance();
    }

    // Classe Review interne
    public static class Review {
        private String userId;
        private String userName;
        private double rating;
        private String comment;
        private long timestamp;

        public Review(String userId, String userName, double rating, String comment) {
            this.userId = userId;
            this.userName = userName;
            this.rating = rating;
            this.comment = comment;
            this.timestamp = System.currentTimeMillis();
        }

        public String getUserId() { return userId; }
        public String getUserName() { return userName; }
        public double getRating() { return rating; }
        public String getComment() { return comment; }
        public long getTimestamp() { return timestamp; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("userName", userName);
            map.put("rating", rating);
            map.put("comment", comment);
            map.put("timestamp", timestamp);
            return map;
        }
    }

    // Interface pour les callbacks
    public interface ReviewCallback {
        void onReviewSubmitted(double newAverageRating);
        void onReviewError(String errorMessage);
    }

    public void submitReview(String userId, String productId, Review review, ReviewCallback callback) {
        try {
            // 1. Ajouter la review dans la sous-collection
            db.collection("products").document(productId)
                    .collection("reviews")
                    .document(userId)
                    .set(review.toMap())
                    .addOnSuccessListener(aVoid -> {
                        // 2. Mettre à jour la note moyenne du produit
                        updateProductRating(productId, review.getRating(), callback);
                    })
                    .addOnFailureListener(e -> {
                        if (callback != null) {
                            callback.onReviewError("Erreur ajout review: " + e.getMessage());
                        }
                    });

        } catch (Exception e) {
            if (callback != null) {
                callback.onReviewError("Erreur: " + e.getMessage());
            }
        }
    }

    private void updateProductRating(String productId, double newRatingValue, ReviewCallback callback) {
        DocumentReference productRef = db.collection("products").document(productId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot productSnapshot = transaction.get(productRef);

            if (!productSnapshot.exists()) {
                try {
                    throw new Exception("Produit non trouvé: " + productId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            // Récupérer les valeurs actuelles (avec valeurs par défaut)
            double currentRating = 0.0;
            long currentCount = 0;

            if (productSnapshot.contains("rating")) {
                Object ratingObj = productSnapshot.get("rating");
                if (ratingObj instanceof Double) {
                    currentRating = (Double) ratingObj;
                } else if (ratingObj instanceof Long) {
                    currentRating = ((Long) ratingObj).doubleValue();
                }
            }

            if (productSnapshot.contains("reviewsCount")) {
                Object countObj = productSnapshot.get("reviewsCount");
                if (countObj instanceof Long) {
                    currentCount = (Long) countObj;
                } else if (countObj instanceof Integer) {
                    currentCount = ((Integer) countObj).longValue();
                }
            }

            // Calculer la nouvelle moyenne
            double newRating = ((currentRating * currentCount) + newRatingValue) / (currentCount + 1);
            long newCount = currentCount + 1;

            // Mettre à jour les champs
            Map<String, Object> updates = new HashMap<>();
            updates.put("rating", newRating);
            updates.put("reviewsCount", newCount);

            transaction.update(productRef, updates);
            return null;

        }).addOnSuccessListener(aVoid -> {
            // Récupérer la nouvelle moyenne pour le callback
            productRef.get().addOnSuccessListener(snapshot -> {
                if (callback != null) {
                    double updatedRating = 0.0;
                    if (snapshot.contains("rating")) {
                        Object ratingObj = snapshot.get("rating");
                        if (ratingObj instanceof Double) {
                            updatedRating = (Double) ratingObj;
                        } else if (ratingObj instanceof Long) {
                            updatedRating = ((Long) ratingObj).doubleValue();
                        }
                    }
                    callback.onReviewSubmitted(updatedRating);
                }
            });

        }).addOnFailureListener(e -> {
            if (callback != null) {
                callback.onReviewError("Erreur transaction: " + e.getMessage());
            }
        });
    }

    // Méthode pour récupérer les reviews d'un produit
    public void getProductReviews(String productId, ReviewsCallback callback) {
        db.collection("products").document(productId)
                .collection("reviews")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (callback != null) {
                        callback.onReviewsLoaded(queryDocumentSnapshots.toObjects(Review.class));
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onReviewsError("Erreur chargement reviews: " + e.getMessage());
                    }
                });
    }

    public interface ReviewsCallback {
        void onReviewsLoaded(List<Review> reviews);
        void onReviewsError(String errorMessage);
    }
}