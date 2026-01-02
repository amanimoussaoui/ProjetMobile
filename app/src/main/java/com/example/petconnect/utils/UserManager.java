package com.example.petconnect.utils;

import android.util.Log;

import com.example.petconnect.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String TAG = "UserManager";
    private static final String USERS_COLLECTION = "users";
    private static UserManager instance;
    private FirebaseFirestore firestore;

    private UserManager() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void createUser(User user, UserCallback callback) {
        DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(user.getUserId());
        userRef.set(user.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User document created successfully");
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating user document", e);
                    callback.onError(e.getMessage());
                });
    }

    public void getUser(String userId, UserCallback callback) {
        firestore.collection(USERS_COLLECTION).document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            User user = null;
                            try {
                                user = document.toObject(User.class);
                                if (user != null) {
                                    // S'assurer que l'userId est défini
                                    user.setUserId(userId);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting document to User", e);
                            }
                            
                            // Si toObject() a échoué ou retourné null, créer manuellement
                            if (user == null) {
                                try {
                                    user = createUserFromDocument(document, userId);
                                } catch (Exception e) {
                                    callback.onError("Erreur lors de la conversion des données utilisateur: " + e.getMessage());
                                    return;
                                }
                            }
                            
                            callback.onSuccess(user);
                        } else {
                            callback.onError("Utilisateur introuvable");
                        }
                    } else {
                        Log.e(TAG, "Error getting user document", task.getException());
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Erreur inconnue");
                    }
                });
    }

    private User createUserFromDocument(DocumentSnapshot document, String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setFirstName(document.getString("firstName"));
        user.setLastName(document.getString("lastName"));
        user.setEmail(document.getString("email"));
        user.setPhoneNumber(document.getString("phoneNumber"));
        user.setAddress(document.getString("address"));
        user.setPhotoUrl(document.getString("photoUrl"));
        user.setRole(document.getString("role"));
        
        Map<String, Boolean> preferences = (Map<String, Boolean>) document.get("animalPreferences");
        if (preferences != null) {
            user.setAnimalPreferences(preferences);
        }
        
        user.setLifestyle(document.getString("lifestyle"));
        user.setHousingType(document.getString("housingType"));
        Boolean hasGarden = document.getBoolean("hasGarden");
        if (hasGarden != null) {
            user.setHasGarden(hasGarden);
        }
        user.setExperienceLevel(document.getString("experienceLevel"));
        user.setAvatarData(document.getString("avatarData"));
        
        Long createdAt = document.getLong("createdAt");
        if (createdAt != null) {
            user.setCreatedAt(createdAt);
        }
        
        Long updatedAt = document.getLong("updatedAt");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt);
        }
        
        return user;
    }

    public void updateUser(User user, UserCallback callback) {
        user.setUpdatedAt(System.currentTimeMillis());
        DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(user.getUserId());
        userRef.update(user.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User document updated successfully");
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user document", e);
                    callback.onError(e.getMessage());
                });
    }

    public void updateUserField(String userId, String field, Object value, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);
        updates.put("updatedAt", System.currentTimeMillis());

        firestore.collection(USERS_COLLECTION).document(userId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User field updated successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user field", e);
                    callback.onError(e.getMessage());
                });
    }

    public void deleteUser(String userId, SimpleCallback callback) {
        firestore.collection(USERS_COLLECTION).document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User document deleted successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user document", e);
                    callback.onError(e.getMessage());
                });
    }

    // Interfaces de callback
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}



