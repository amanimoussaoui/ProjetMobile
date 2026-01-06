package com.example.petconnect.modules.shop.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.petconnect.modules.shop.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartService {
    private static CartService instance;
    private final FirebaseFirestore db;
    private String userId;
    private List<CartItem> cartItems = new ArrayList<>();

    // Interface pour les callbacks
    public interface CartCallback {
        void onSuccess();
        void onError(String error);
    }

    // Classe interne pour les items du panier
    public static class CartItem {
        public Product product;
        public int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public double getItemTotal() {
            if (product == null) {
                return 0.0;
            }
            return product.getPrice() * quantity;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();

            if (product == null) {
                Log.e("CartItem", "Product is null in toMap()");
                map.put("quantity", quantity);
                map.put("productId", "");
                map.put("productName", "");
                map.put("price", 0.0);
                map.put("imageUrl", "");
                map.put("category", "");
                map.put("description", "");
                return map;
            }

            map.put("productId", product.getId() != null ? product.getId() : "");
            map.put("productName", product.getName() != null ? product.getName() : "");
            map.put("price", product.getPrice());
            map.put("quantity", quantity);

            // CORRECTION PRINCIPALE : Vérifier correctement les valeurs null
            String imageUrl = String.valueOf(product.getImageUrl());
            map.put("imageUrl", imageUrl != null ? imageUrl : "");

            String category = product.getCategory();
            map.put("category", category != null ? category : "");

            String description = product.getDescription();
            map.put("description", description != null ? description : "");

            return map;
        }

        public static CartItem fromMap(Map<String, Object> map) {
            if (map == null) {
                Log.e("CartItem", "Map is null in fromMap()");
                return null;
            }

            Product product = new Product();

            // ID
            Object idObj = map.get("productId");
            product.setId(idObj != null ? idObj.toString() : "");

            // Nom
            Object nameObj = map.get("productName");
            product.setName(nameObj != null ? nameObj.toString() : "");

            // Prix
            Object priceObj = map.get("price");
            double price = 0.0;
            if (priceObj != null) {
                if (priceObj instanceof Double) {
                    price = (Double) priceObj;
                } else if (priceObj instanceof Long) {
                    price = ((Long) priceObj).doubleValue();
                } else if (priceObj instanceof Integer) {
                    price = ((Integer) priceObj).doubleValue();
                } else {
                    try {
                        price = Double.parseDouble(priceObj.toString());
                    } catch (NumberFormatException e) {
                        Log.e("CartItem", "Error parsing price: " + e.getMessage());
                    }
                }
            }
            product.setPrice(price);

            // Image URL - CORRECTION : gérer le null correctement
            Object imageUrlObj = map.get("imageUrl");
            if (imageUrlObj != null) {
                product.setImageUrl(imageUrlObj.toString());
            } else {
                product.setImageUrl("");
            }

            // Catégorie - CORRECTION : gérer le null correctement
            Object categoryObj = map.get("category");
            if (categoryObj != null) {
                product.setCategory(categoryObj.toString());
            } else {
                product.setCategory("");
            }

            // Description - CORRECTION : gérer le null correctement
            Object descriptionObj = map.get("description");
            if (descriptionObj != null) {
                product.setDescription(descriptionObj.toString());
            } else {
                product.setDescription("");
            }

            // Quantité - CORRECTION CRITIQUE : gérer le null pour int
            Object qtyObj = map.get("quantity");
            int quantity = 1;
            if (qtyObj != null) {
                if (qtyObj instanceof Long) {
                    quantity = ((Long) qtyObj).intValue();
                } else if (qtyObj instanceof Integer) {
                    quantity = (Integer) qtyObj;
                } else if (qtyObj instanceof Double) {
                    quantity = ((Double) qtyObj).intValue();
                } else {
                    try {
                        quantity = Integer.parseInt(qtyObj.toString());
                    } catch (NumberFormatException e) {
                        Log.e("CartItem", "Error parsing quantity: " + e.getMessage());
                    }
                }
            }

            return new CartItem(product, quantity);
        }
    }

    // Singleton pattern
    public static CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    private CartService() {
        db = FirebaseFirestore.getInstance();
        updateUserId();
    }

    /**
     * Mettre à jour le userId (appelé lors des changements de connexion)
     */
    private void updateUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("CartService", "Utilisateur connecté: " + userId);
        } else {
            userId = "local_user";
            Log.d("CartService", "Utilisateur local (non connecté)");
        }
    }

    // ==========================================
    // MÉTHODES PRINCIPALES
    // ==========================================

    /**
     * Ajouter un produit au panier avec quantité par défaut (méthode simplifiée)
     */
    public void add(Product product) {
        if (product == null) {
            Log.e("CartService", "Produit null dans add()");
            return;
        }

        addToCart(product, 1, new CartCallback() {
            @Override
            public void onSuccess() {
                Log.d("CartService", "Produit ajouté avec succès: " + product.getName());
            }

            @Override
            public void onError(String error) {
                Log.e("CartService", "Erreur ajout produit: " + error);
            }
        });
    }

    /**
     * Ajouter un produit au panier (avec Firebase)
     */
    public void addToCart(Product product, int quantity, CartCallback callback) {
        if (product == null) {
            Log.e("CartService", "Produit null dans addToCart()");
            if (callback != null) {
                callback.onError("Produit invalide");
            }
            return;
        }

        // Mettre à jour le userId avant l'ajout
        updateUserId();
        Log.d("CartService", "Ajout au panier: " + product.getName() + " x" + quantity);

        // Vérifier si le produit est déjà dans le panier
        for (CartItem item : cartItems) {
            if (item.product != null && item.product.getId() != null &&
                    item.product.getId().equals(product.getId())) {
                // Mettre à jour la quantité
                updateQuantity(product.getId(), item.quantity + quantity, callback);
                return;
            }
        }

        // Ajouter localement
        CartItem newItem = new CartItem(product, quantity);
        cartItems.add(newItem);

        // Sauvegarder uniquement ce produit dans Firebase
        saveItemToFirebase(newItem, callback);
    }

    /**
     * Supprimer un produit du panier
     */
    public void removeFromCart(String productId, CartCallback callback) {
        if (productId == null || productId.isEmpty()) {
            Log.e("CartService", "ProductId null ou vide dans removeFromCart()");
            if (callback != null) {
                callback.onError("ID produit invalide");
            }
            return;
        }

        // Mettre à jour le userId avant la suppression
        updateUserId();
        
        Log.d("CartService", "Suppression du panier: " + productId);

        // Supprimer localement d'abord
        cartItems.removeIf(item -> item.product != null &&
                item.product.getId() != null &&
                item.product.getId().equals(productId));

        // Supprimer de Firebase
        db.collection("PetConnect")
                .document("cart")
                .collection(userId)
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("CartService", "✅ Produit supprimé de Firebase: " + productId);
                    Log.d("CartService", "   Chemin: PetConnect/cart/" + userId + "/" + productId);
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartService", "❌ Erreur suppression Firebase: " + e.getMessage());
                    Log.e("CartService", "   Chemin: PetConnect/cart/" + userId + "/" + productId);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    /**
     * Mettre à jour la quantité d'un produit
     */
    public void updateQuantity(String productId, int newQuantity, CartCallback callback) {
        if (productId == null || productId.isEmpty()) {
            Log.e("CartService", "ProductId null ou vide dans updateQuantity()");
            if (callback != null) {
                callback.onError("ID produit invalide");
            }
            return;
        }

        Log.d("CartService", "Mise à jour quantité: " + productId + " -> " + newQuantity);

        if (newQuantity <= 0) {
            // Supprimer si quantité = 0
            removeFromCart(productId, callback);
            return;
        }

        // Trouver l'item et mettre à jour localement
        CartItem foundItem = null;
        for (CartItem item : cartItems) {
            if (item.product != null &&
                    item.product.getId() != null &&
                    item.product.getId().equals(productId)) {
                item.quantity = newQuantity;
                foundItem = item;
                break;
            }
        }

        if (foundItem == null) {
            Log.e("CartService", "Produit non trouvé dans le panier: " + productId);
            if (callback != null) callback.onError("Produit non trouvé dans le panier");
            return;
        }

        // Mettre à jour le userId avant la sauvegarde
        updateUserId();
        
        // Sauvegarder uniquement ce produit dans Firebase avec merge pour préserver addedAt
        saveItemToFirebaseWithMerge(foundItem, callback);
    }
    
    /**
     * Sauvegarder un item avec merge pour préserver les champs existants comme addedAt
     */
    private void saveItemToFirebaseWithMerge(CartItem item, CartCallback callback) {
        if (item == null || item.product == null || item.product.getId() == null) {
            Log.e("CartService", "Item invalide pour sauvegarde Firebase");
            if (callback != null) callback.onError("Item invalide");
            return;
        }

        // Mettre à jour le userId avant la sauvegarde
        updateUserId();

        // S'assurer que les documents parent existent d'abord
        ensureParentDocuments();

        Map<String, Object> itemData = item.toMap();
        itemData.put("updatedAt", FieldValue.serverTimestamp());
        
        String productId = item.product.getId();
        String productName = item.product.getName() != null ? item.product.getName() : "Sans nom";
        
        // Utiliser merge pour préserver les champs existants (comme addedAt)
        db.collection("PetConnect")
                .document("cart")
                .collection(userId)
                .document(productId)
                .set(itemData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("CartService", "✅ Item mis à jour dans Firebase: " + productName + 
                            " (ID: " + productId + ", Qty: " + item.quantity + ")");
                    Log.d("CartService", "   Chemin: PetConnect/cart/" + userId + "/" + productId);
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartService", "❌ Erreur mise à jour Firebase: " + e.getMessage());
                    Log.e("CartService", "   Produit: " + productName + " (ID: " + productId + ")");
                    Log.e("CartService", "   Chemin: PetConnect/cart/" + userId + "/" + productId);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    /**
     * Charger le panier depuis Firebase
     */
    public void loadFromFirebase(CartCallback callback) {
        // Mettre à jour le userId avant le chargement
        updateUserId();
        Log.d("CartService", "Chargement panier depuis Firebase pour: " + userId);

        db.collection("PetConnect")
                .document("cart")
                .collection(userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("CartService", "Panier vide dans Firebase");
                        if (callback != null) callback.onSuccess();
                        return;
                    }

                    Log.d("CartService", "Items trouvés: " + queryDocumentSnapshots.size());

                    // Pour chaque document, utiliser directement les données sauvegardées
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        if (data == null || data.isEmpty()) {
                            Log.w("CartService", "Document vide ou null: " + doc.getId());
                            continue;
                        }

                        try {
                            CartItem item = CartItem.fromMap(data);
                            if (item != null && item.product != null) {
                                cartItems.add(item);
                                Log.d("CartService", "Produit chargé depuis Firebase: " +
                                        item.product.getName() + " x" + item.quantity);
                            }
                        } catch (Exception e) {
                            Log.e("CartService", "Erreur parsing item " + doc.getId() + ": " + e.getMessage());
                        }
                    }

                    Log.d("CartService", "Panier chargé: " + cartItems.size() + " items");
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartService", "Erreur chargement Firebase: " + e.getMessage());
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    /**
     * Vider le panier complètement
     */
    public void clear() {
        Log.d("CartService", "Vidage du panier");

        // Supprimer tous les documents dans Firebase
        db.collection("PetConnect")
                .document("cart")
                .collection(userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                });

        // Vider localement
        cartItems.clear();
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Créer les documents parent PetConnect et Cart dans Firebase si nécessaire
     */
    private void ensureParentDocuments() {
        // Créer le document parent "PetConnect" s'il n'existe pas
        Map<String, Object> petConnectData = new HashMap<>();
        petConnectData.put("appName", "PetConnect");
        petConnectData.put("lastUpdated", FieldValue.serverTimestamp());
        petConnectData.put("version", "1.0");
        db.collection("PetConnect")
                .document("_info")
                .set(petConnectData)
                .addOnSuccessListener(aVoid -> Log.d("CartService", "Document PetConnect créé/mis à jour"))
                .addOnFailureListener(e -> Log.w("CartService", "Erreur création document PetConnect: " + e.getMessage()));

        // Créer le document parent "cart" s'il n'existe pas
        Map<String, Object> cartData = new HashMap<>();
        cartData.put("cartName", "Cart");
        cartData.put("lastUpdated", FieldValue.serverTimestamp());
        cartData.put("userId", userId);
        db.collection("PetConnect")
                .document("cart")
                .set(cartData)
                .addOnSuccessListener(aVoid -> Log.d("CartService", "Document Cart créé/mis à jour"))
                .addOnFailureListener(e -> Log.w("CartService", "Erreur création document Cart: " + e.getMessage()));
    }

    /**
     * Sauvegarder un seul item dans Firebase (méthode optimisée)
     */
    private void saveItemToFirebase(CartItem item, CartCallback callback) {
        if (item == null || item.product == null || item.product.getId() == null) {
            Log.e("CartService", "Item invalide pour sauvegarde Firebase");
            if (callback != null) callback.onError("Item invalide");
            return;
        }

        // Mettre à jour le userId avant la sauvegarde
        updateUserId();

        // S'assurer que les documents parent existent d'abord
        ensureParentDocuments();

        Map<String, Object> itemData = item.toMap();
        
        // Toujours ajouter addedAt pour un nouvel item
        itemData.put("addedAt", FieldValue.serverTimestamp());
        itemData.put("updatedAt", FieldValue.serverTimestamp());
        
        String productId = item.product.getId();
        String productName = item.product.getName() != null ? item.product.getName() : "Sans nom";
        
        // Sauvegarder l'item individuellement avec set (écrase complètement)
        db.collection("PetConnect")
                .document("cart")
                .collection(userId)
                .document(productId)
                .set(itemData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("CartService", "✅ Item sauvegardé dans Firebase: " + productName + 
                            " (ID: " + productId + ", Qty: " + item.quantity + ")");
                    Log.d("CartService", "   Chemin: PetConnect/cart/" + userId + "/" + productId);
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartService", "❌ Erreur sauvegarde Firebase: " + e.getMessage());
                    Log.e("CartService", "   Produit: " + productName + " (ID: " + productId + ")");
                    Log.e("CartService", "   Chemin: PetConnect/cart/" + userId + "/" + productId);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public List<CartItem> getItems() {
        return new ArrayList<>(cartItems);
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item != null) {
                total += item.getItemTotal();
            }
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            if (item != null) {
                count += item.quantity;
            }
        }
        return count;
    }

    /**
     * Récupérer la quantité d'un produit spécifique
     */
    public int getQuantityForProduct(String productId) {
        if (productId == null || productId.isEmpty()) {
            return 0;
        }

        for (CartItem item : cartItems) {
            if (item != null && item.product != null &&
                    item.product.getId() != null &&
                    item.product.getId().equals(productId)) {
                return item.quantity;
            }
        }
        return 0;
    }

    /**
     * Vérifier si le panier contient un produit
     */
    public boolean containsProduct(String productId) {
        if (productId == null || productId.isEmpty()) {
            return false;
        }

        for (CartItem item : cartItems) {
            if (item != null && item.product != null &&
                    item.product.getId() != null &&
                    item.product.getId().equals(productId)) {
                return true;
            }
        }
        return false;
    }
}
