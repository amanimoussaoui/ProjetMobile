package com.example.petconnect.modules.shop.services;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.petconnect.modules.shop.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirebaseService {
    private static final String TAG = "FirebaseService";
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String userId;

    public FirebaseService() {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();

        // Récupérer l'utilisateur courant
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            this.userId = currentUser.getUid();
            Log.d(TAG, "Utilisateur connecté: " + userId);
        } else {
            this.userId = "admin_user"; // Fallback pour admin
            Log.d(TAG, "Utilisateur non connecté, fallback à: " + userId);
        }
    }

    // =========================
    // INTERFACES DE CALLBACK
    // =========================

    public interface UploadImageCallback {
        void onImageUploaded(String imageUrl);
        void onError(String error);
    }

    public interface AddProductCallback {
        void onProductAdded(String productId);
        void onError(String error);
    }

    public interface ProductsCallback {
        void onProductsLoaded(List<Product> products);
        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }

    // =========================
    // MÉTHODES PRINCIPALES
    // =========================

    /**
     * Méthode principale pour ajouter un produit avec image
     */
    public void addProductWithImage(Product product, Uri imageUri, AddProductCallback callback) {
        Log.d(TAG, "Début addProductWithImage - Nom: " + product.getName());

        if (imageUri != null) {
            Log.d(TAG, "Image URI fournie: " + imageUri.toString());
            uploadProductImage(imageUri, product.getName(), new UploadImageCallback() {
                @Override
                public void onImageUploaded(String imageUrl) {
                    Log.d(TAG, "Image téléchargée avec succès: " + imageUrl);
                    product.setImageUrl(imageUrl); // UNIQUEMENT setImageUrl
                    addProductToFirestore(product, callback);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Erreur upload image: " + error);
                    callback.onError("Erreur upload image: " + error);
                }
            });
        } else {
            Log.d(TAG, "Aucune image fournie, ajout du produit sans image");
            addProductToFirestore(product, callback);
        }
    }

    /**
     * Télécharger une image vers Firebase Storage
     */
    public void uploadProductImage(Uri imageUri, String productName, UploadImageCallback callback) {
        if (imageUri == null) {
            callback.onError("Aucune image sélectionnée");
            return;
        }

        try {
            // Créer un nom de fichier unique
            String fileName = "products/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storage.getReference().child(fileName);

            Log.d(TAG, "Début upload image vers: " + fileName);

            // Télécharger l'image
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "Image uploadée avec succès");

                        // Récupérer l'URL de téléchargement
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    Log.d(TAG, "URL image obtenue: " + imageUrl);
                                    callback.onImageUploaded(imageUrl);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Erreur récupération URL: " + e.getMessage());
                                    callback.onError("Erreur récupération URL: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erreur téléchargement image: " + e.getMessage());
                        callback.onError("Erreur téléchargement: " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception upload image: " + e.getMessage());
            callback.onError("Exception: " + e.getMessage());
        }
    }

    /**
     * Ajouter un produit dans Firestore (méthode interne)
     */
    private void addProductToFirestore(Product product, AddProductCallback callback) {
        try {
            Log.d(TAG, "Début addProductToFirestore - Produit: " + product.getName());
            Log.d(TAG, "Prix: " + product.getPrice());
            Log.d(TAG, "Catégorie: " + product.getCategory());
            Log.d(TAG, "User ID: " + userId);

            // Préparer les données du produit
            Map<String, Object> productData = new HashMap<>();
            productData.put("name", product.getName());
            productData.put("price", product.getPrice());
            productData.put("category", product.getCategory());
            productData.put("description", product.getDescription());
            productData.put("imageUrl", product.getImageUrl()); // UNIQUEMENT imageUrl
            productData.put("stockQuantity", product.getStockQuantity());
            productData.put("rating", product.getRating());
            productData.put("isAvailable", product.isAvailable());
            productData.put("createdAt", FieldValue.serverTimestamp());
            productData.put("updatedAt", FieldValue.serverTimestamp());
            productData.put("createdBy", userId);
            productData.put("totalSales", 0);
            productData.put("salesCount", 0);

            Log.d(TAG, "Données du produit prêtes pour Firestore");

            // Ajouter dans la collection "products"
            db.collection("products")
                    .add(productData)
                    .addOnSuccessListener(documentReference -> {
                        String productId = documentReference.getId();
                        Log.d(TAG, "✅ Produit ajouté avec succès! ID: " + productId);
                        Log.d(TAG, "Chemin Firestore: products/" + productId);

                        // Mettre à jour l'ID du produit
                        product.setId(productId);

                        if (callback != null) {
                            callback.onProductAdded(productId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "❌ Erreur ajout produit Firestore: " + e.getMessage());

                        // Tentative alternative avec ID personnalisé
                        String customId = "prod_" + System.currentTimeMillis();

                        db.collection("products")
                                .document(customId)
                                .set(productData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "✅ Produit ajouté avec ID personnalisé: " + customId);
                                    product.setId(customId);

                                    if (callback != null) {
                                        callback.onProductAdded(customId);
                                    }
                                })
                                .addOnFailureListener(e2 -> {
                                    Log.e(TAG, "❌ Échec même avec ID personnalisé: " + e2.getMessage());
                                    if (callback != null) {
                                        callback.onError("Erreur: " + e.getMessage());
                                    }
                                });
                    });
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception dans addProductToFirestore: " + e.getMessage());
            if (callback != null) {
                callback.onError("Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Récupérer tous les produits depuis Firestore
     */
    public void getAllProducts(final ProductsCallback callback) {
        Log.d(TAG, "Début getAllProducts");

        db.collection("products")
                .whereEqualTo("isAvailable", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "✅ Firestore - Produits récupérés: " + queryDocumentSnapshots.size());

                    List<Product> products = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = convertToProduct(doc);
                        if (product != null) {
                            products.add(product);
                            Log.d(TAG, "Produit chargé: " + product.getName() + " (ID: " + product.getId() + ")");
                        }
                    }

                    if (callback != null) {
                        Log.d(TAG, "✅ Total produits chargés: " + products.size());
                        callback.onProductsLoaded(products);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur chargement produits Firestore: " + e.getMessage());

                    if (callback != null) {
                        callback.onError("Erreur chargement: " + e.getMessage());
                    }
                });
    }

    /**
     * Convertir DocumentSnapshot en objet Product
     */
    private Product convertToProduct(DocumentSnapshot doc) {
        try {
            String id = doc.getId();
            Log.d(TAG, "Conversion document ID: " + id);

            Product product = new Product();
            product.setId(id);
            product.setName(doc.getString("name"));

            // Gérer le prix
            Object priceObj = doc.get("price");
            double price = 0.0;

            if (priceObj instanceof Double) {
                price = (Double) priceObj;
            } else if (priceObj instanceof Long) {
                price = ((Long) priceObj).doubleValue();
            } else if (priceObj instanceof Integer) {
                price = ((Integer) priceObj).doubleValue();
            }
            product.setPrice(price);

            product.setCategory(doc.getString("category"));
            product.setDescription(doc.getString("description"));

            // Utiliser uniquement imageUrl
            String imageUrl = doc.getString("imageUrl");
            product.setImageUrl(imageUrl);

            // Stock
            Object stockObj = doc.get("stockQuantity");
            int stock = 10;
            if (stockObj instanceof Long) {
                stock = ((Long) stockObj).intValue();
            } else if (stockObj instanceof Integer) {
                stock = (Integer) stockObj;
            }
            product.setStockQuantity(stock);

            // Rating
            Object ratingObj = doc.get("rating");
            double rating = 4.5;
            if (ratingObj instanceof Double) {
                rating = (Double) ratingObj;
            } else if (ratingObj instanceof Long) {
                rating = ((Long) ratingObj).doubleValue();
            }
            product.setRating(rating);

            // Disponibilité
            Boolean available = doc.getBoolean("isAvailable");
            product.setAvailable(available != null ? available : true);

            Log.d(TAG, "Produit converti: " + product.getName() + " (Prix: " + product.getPrice() + ")");
            return product;

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur conversion document en Product: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ajouter un produit simple (sans image)
     */
    public void addProduct(Product product, SimpleCallback callback) {
        Log.d(TAG, "Début addProduct simple: " + product.getName());

        addProductWithImage(product, null, new AddProductCallback() {
            @Override
            public void onProductAdded(String productId) {
                Log.d(TAG, "✅ Produit ajouté avec ID: " + productId);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "❌ Erreur addProduct: " + error);
                if (callback != null) {
                    callback.onError(error);
                }
            }
        });
    }

    /**
     * Mettre à jour un produit existant
     */
    public void updateProduct(Product product, SimpleCallback callback) {
        if (product.getId() == null || product.getId().isEmpty()) {
            Log.e(TAG, "❌ ID produit manquant pour mise à jour");
            if (callback != null) {
                callback.onError("ID produit manquant");
            }
            return;
        }

        Log.d(TAG, "Début updateProduct ID: " + product.getId());

        Map<String, Object> productData = new HashMap<>();
        productData.put("name", product.getName());
        productData.put("price", product.getPrice());
        productData.put("category", product.getCategory());
        productData.put("description", product.getDescription());
        productData.put("imageUrl", product.getImageUrl());
        productData.put("stockQuantity", product.getStockQuantity());
        productData.put("rating", product.getRating());
        productData.put("isAvailable", product.isAvailable());
        productData.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("products")
                .document(product.getId())
                .update(productData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Produit mis à jour: " + product.getId());
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur mise à jour produit: " + e.getMessage());
                    if (callback != null) {
                        callback.onError("Erreur mise à jour: " + e.getMessage());
                    }
                });
    }

    /**
     * Supprimer un produit
     */
    public void deleteProduct(String productId, SimpleCallback callback) {
        if (productId == null || productId.isEmpty()) {
            if (callback != null) {
                callback.onError("ID produit manquant");
            }
            return;
        }

        Log.d(TAG, "Début deleteProduct ID: " + productId);

        db.collection("products")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Produit supprimé: " + productId);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Erreur suppression produit: " + e.getMessage());
                    if (callback != null) {
                        callback.onError("Erreur suppression: " + e.getMessage());
                    }
                });
    }

    /**
     * Méthode de test pour vérifier la connexion Firebase
     */
    public void testFirebaseConnection(SimpleCallback callback) {
        Log.d(TAG, "Test connexion Firebase...");

        Map<String, Object> testData = new HashMap<>();
        testData.put("test", true);
        testData.put("timestamp", FieldValue.serverTimestamp());
        testData.put("message", "Test de connexion Firebase");
        testData.put("app", "PetConnect");

        String testId = "test_" + System.currentTimeMillis();

        db.collection("connection_tests")
                .document(testId)
                .set(testData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Test connexion Firebase RÉUSSI!");
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Test connexion Firebase ÉCHEC: " + e.getMessage());
                    if (callback != null) {
                        callback.onError("Erreur connexion: " + e.getMessage());
                    }
                });
    }
}
