package com.example.petconnect.adoption.utils;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper pour ajouter les données de test (pets) dans Firestore
 * Usage: FirestoreDataHelper.addAllPetsToFirestore(FirebaseFirestore.getInstance());
 */
public class FirestoreDataHelper {

    private static final String TAG = "FirestoreDataHelper";

    /**
     * Ajoute tous les 6 pets d'exemple dans Firestore
     * Cette méthode utilise une WriteBatch pour ajouter tous les documents en une seule transaction
     */
    public static void addAllPetsToFirestore(FirebaseFirestore db) {
        Log.d(TAG, "Début de l'ajout des pets dans Firestore...");

        WriteBatch batch = db.batch();

        // Créer les données pour les 6 pets
        List<Map<String, Object>> petsData = createPetsData();

        // Ajouter chaque pet dans le batch
        for (int i = 0; i < petsData.size(); i++) {
            String docId = "pet_" + String.format("%03d", i + 1);
            batch.set(db.collection("pets").document(docId), petsData.get(i));
            Log.d(TAG, "Pet ajouté au batch: " + docId);
        }

        // Exécuter le batch
        batch.commit()
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "✅ SUCCESS: Tous les " + petsData.size() + " pets ajoutés avec succès!");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "❌ ERROR: Erreur lors de l'ajout des pets: " + e.getMessage());
            });
    }

    /**
     * Crée la liste de tous les pets avec leurs données
     */
    private static List<Map<String, Object>> createPetsData() {
        List<Map<String, Object>> pets = new ArrayList<>();

        // Pet 1: Max (Labrador)
        pets.add(createPetMap(
            "Max",
            "Labrador Retriever",
            3,
            "Mâle",
            32,
            "Marron",
            "Labrador brun très affectueux et énergique. Adorable avec les enfants, parfait pour la famille. Aime les jeux et la baignade.",
            "https://images.unsplash.com/photo-1633722715463-d30628cfa6a8?w=500",
            "Disponible",
            true,
            true,
            150,
            "Refuge Paris",
            Arrays.asList("Actif", "Affectueux", "Intelligent", "Famille")
        ));

        // Pet 2: Luna (Berger Allemand)
        pets.add(createPetMap(
            "Luna",
            "Berger Allemand",
            2,
            "Femelle",
            28,
            "Noir et Feu",
            "Berger Allemand noir et feu, très dressée et obéissante. Garde et protection naturelle. Excellente pour sécurité et protection.",
            "https://images.unsplash.com/photo-1568572933382-74d440642117?w=500",
            "Disponible",
            true,
            true,
            200,
            "Refuge Lyon",
            Arrays.asList("Protecteur", "Obéissant", "Intelligent", "Vigilant")
        ));

        // Pet 3: Milo (Golden Retriever)
        pets.add(createPetMap(
            "Milo",
            "Golden Retriever",
            4,
            "Mâle",
            35,
            "Roux doré",
            "Golden Retriever roux, très sociable et doux. Excellent avec les enfants et autres animaux. Idéal pour familles aimantes.",
            "https://images.unsplash.com/photo-1600011689520-08ab36fd3f37?w=500",
            "Disponible",
            true,
            true,
            180,
            "Refuge Marseille",
            Arrays.asList("Doux", "Sociable", "Actif", "Aimant")
        ));

        // Pet 4: Bella (Chat Persan)
        pets.add(createPetMap(
            "Bella",
            "Chat Persan",
            2,
            "Femelle",
            4,
            "Blanc et gris",
            "Adorable chat Persan blanc et gris. Calme et affectueux, adore les caresses et les endroits confortables.",
            "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=500",
            "Disponible",
            true,
            true,
            80,
            "Refuge Paris",
            Arrays.asList("Calme", "Affectueux", "Maison", "Doux")
        ));

        // Pet 5: Simba (Chat Noir)
        pets.add(createPetMap(
            "Simba",
            "Chat Européen",
            1,
            "Mâle",
            3,
            "Noir",
            "Petit chat noir tout mignon, très joueur et curieux. Idéal pour appartement. Aime les jeux et l'exploration.",
            "https://images.unsplash.com/photo-1573865526014-f3550b887b70?w=500",
            "Disponible",
            true,
            false,
            50,
            "Refuge Lyon",
            Arrays.asList("Joueur", "Curieux", "Affectueux", "Jeune")
        ));

        // Pet 6: Charlie (Cocker Spaniel)
        pets.add(createPetMap(
            "Charlie",
            "Cocker Spaniel",
            5,
            "Mâle",
            15,
            "Noir",
            "Cocker noir très obéissant et affectueux. Senior mais encore très actif et joueur. Calme mais plein de vie.",
            "https://images.unsplash.com/photo-1619036633304-2284e1b2b61b?w=500",
            "Disponible",
            true,
            true,
            120,
            "Refuge Toulouse",
            Arrays.asList("Affectueux", "Obéissant", "Joueur", "Senior")
        ));

        return pets;
    }

    /**
     * Crée une Map avec tous les champs d'un pet
     */
    private static Map<String, Object> createPetMap(
        String name,
        String breed,
        int age,
        String gender,
        double weight,
        String color,
        String description,
        String image,
        String adoptionStatus,
        boolean vaccinated,
        boolean neutered,
        int adoptionFee,
        String location,
        List<String> features) {

        Map<String, Object> pet = new HashMap<>();
        pet.put("name", name);
        pet.put("breed", breed);
        pet.put("age", age);
        pet.put("gender", gender);
        pet.put("weight", weight);
        pet.put("color", color);
        pet.put("description", description);
        pet.put("image", image);
        pet.put("adoptionStatus", adoptionStatus);
        pet.put("vaccinated", vaccinated);
        pet.put("neutered", neutered);
        pet.put("adoptionFee", adoptionFee);
        pet.put("location", location);
        pet.put("features", features);
        pet.put("createdAt", System.currentTimeMillis());

        return pet;
    }

    /**
     * Méthode alternative: Ajouter les pets un par un (sans batch)
     * Moins efficace mais peut être utile si batch ne fonctionne pas
     */
    public static void addAllPetsOneByOne(FirebaseFirestore db) {
        Log.d(TAG, "Ajout des pets un par un...");

        List<Map<String, Object>> petsData = createPetsData();

        for (int i = 0; i < petsData.size(); i++) {
            final int index = i;
            String docId = "pet_" + String.format("%03d", i + 1);

            db.collection("pets")
                .document(docId)
                .set(petsData.get(i))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Pet " + (index + 1) + " ajouté: " + docId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur pour pet " + (index + 1) + ": " + e.getMessage());
                });
        }
    }

    /**
     * Vérifier combien de pets sont actuellement dans Firestore
     */
    public static void checkPetsCount(FirebaseFirestore db) {
        db.collection("pets")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                int count = queryDocumentSnapshots.size();
                Log.d(TAG, "📊 Nombre de pets dans Firestore: " + count);

                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                    String name = queryDocumentSnapshots.getDocuments().get(i).getString("name");
                    String breed = queryDocumentSnapshots.getDocuments().get(i).getString("breed");
                    Log.d(TAG, "  " + (i + 1) + ". " + name + " - " + breed);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "❌ Erreur lors de la vérification: " + e.getMessage());
            });
    }

    /**
     * Supprimer tous les pets (utile pour réinitialiser)
     */
    public static void deleteAllPets(FirebaseFirestore db) {
        Log.d(TAG, "Suppression de tous les pets...");

        db.collection("pets")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                WriteBatch batch = db.batch();

                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                    batch.delete(queryDocumentSnapshots.getDocuments().get(i).getReference());
                }

                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "✅ Tous les pets supprimés");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "❌ Erreur lors de la suppression: " + e.getMessage());
                    });
            });
    }
}
