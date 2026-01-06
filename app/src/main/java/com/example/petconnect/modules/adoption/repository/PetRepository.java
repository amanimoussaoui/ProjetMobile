package com.example.petconnect.modules.adoption.repository;

import android.util.Log;
import com.example.petconnect.modules.adoption.model.Pet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class PetRepository {
    private static final String TAG = "PetRepository";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface PetCallback {
        void onPetsLoaded(List<Pet> pets);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }

    public void loadPets(PetCallback callback) {
        Log.d(TAG, "🔥 Fetching pets from Firestore collection: 'pet'");
        Log.d(TAG, "🔥 Firestore instance: " + db.toString());

        db.collection("pet")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Pet> pets = new ArrayList<>();

                    Log.d(TAG, "✅ Firestore query successful");
                    Log.d(TAG, "📦 Documents found: " + querySnapshot.size());
                    Log.d(TAG, "📦 Is empty? " + querySnapshot.isEmpty());

                    if (querySnapshot.isEmpty()) {
                        Log.w(TAG, "⚠️ No documents in 'pet' collection!");
                        callback.onPetsLoaded(pets);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        try {
                            Pet pet = doc.toObject(Pet.class);
                            // Stocker l'ID Firestore dans le Pet
                            pet.setFirestoreId(doc.getId());
                            pet.setId(doc.getId().hashCode());
                            pets.add(pet);

                            Log.d(TAG, "✅ Pet loaded: " + pet.getName() +
                                    " (ID: " + doc.getId() + ")");
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Error parsing pet " + doc.getId() +
                                    ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    Log.d(TAG, "🎉 Total pets loaded: " + pets.size());
                    callback.onPetsLoaded(pets);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Firestore query failed: " + e.getMessage());
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Supprime un animal de la base de données Firestore
     * @param firestoreId L'ID du document Firestore
     * @param callback Callback pour gérer le succès ou l'erreur
     */
    public void deletePet(String firestoreId, DeleteCallback callback) {
        if (firestoreId == null || firestoreId.isEmpty()) {
            Log.e(TAG, "❌ Cannot delete pet: Firestore ID is null or empty");
            callback.onError("ID invalide");
            return;
        }

        Log.d(TAG, "🗑️ Deleting pet with Firestore ID: " + firestoreId);

        db.collection("pet")
                .document(firestoreId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Pet deleted successfully: " + firestoreId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to delete pet: " + e.getMessage());
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                });
    }
}
