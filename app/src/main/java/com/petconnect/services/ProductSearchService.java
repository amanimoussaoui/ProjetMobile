package com.petconnect.services;

import android.util.Log;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchService {
    private static final String TAG = "ProductSearchService";
    private static final String ALGOLIA_APP_ID = "YOUR_APP_ID"; // À remplacer
    private static final String ALGOLIA_API_KEY = "YOUR_API_KEY"; // À remplacer
    private static final String INDEX_NAME = "your_index_name"; // À remplacer

    private Client algoliaClient;
    private Index index;

    public ProductSearchService() {
        try {
            // 1. Initialiser le client Algolia
            algoliaClient = new Client(ALGOLIA_APP_ID, ALGOLIA_API_KEY);
            // 2. Obtenir l'index
            index = algoliaClient.getIndex(INDEX_NAME);
            Log.d(TAG, "Service Algolia initialisé avec succès.");
        } catch (Exception e) {
            Log.e(TAG, "Erreur d'initialisation d'Algolia: " + e.getMessage());
        }
    }

    // Interface pour le callback de résultat
    public interface OnSearchResultListener {
        void onSearchResult(List<ProductSearchResult> results);
        void onSearchError(String errorMessage);
    }

    public void searchProducts(String queryText, final OnSearchResultListener listener) {
        if (index == null || queryText == null || queryText.trim().isEmpty()) {
            listener.onSearchError("Service ou requête invalide.");
            return;
        }

        // 1. Créer la requête Algolia
        Query query = new Query(queryText)
                .setAttributesToRetrieve("name", "description", "price", "category", "imageUrl", "objectID")
                .setHitsPerPage(20);

        // 2. Exécuter la recherche
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if (error != null) {
                    // En cas d'erreur
                    listener.onSearchError("Erreur de recherche: " + error.getMessage());
                    return;
                }

                try {
                    // 3. Parser les résultats
                    List<ProductSearchResult> results = new ArrayList<>();
                    JSONArray hits = content.getJSONArray("hits");

                    for (int i = 0; i < hits.length(); i++) {
                        JSONObject hit = hits.getJSONObject(i);
                        ProductSearchResult result = new ProductSearchResult(
                                hit.getString("objectID"),
                                hit.optString("name", ""),
                                hit.optString("description", ""),
                                hit.optDouble("price", 0.0),
                                hit.optString("category", ""),
                                hit.optString("imageUrl", "")
                        );
                        results.add(result);
                    }

                    // 4. Retourner les résultats
                    listener.onSearchResult(results);

                } catch (JSONException e) {
                    listener.onSearchError("Erreur de traitement des données: " + e.getMessage());
                }
            }
        });
    }

    // Classe pour modéliser un résultat de recherche
    public static class ProductSearchResult {
        public String id;
        public String name;
        public String description;
        public double price;
        public String category;
        public String imageUrl;

        public ProductSearchResult(String id, String name, String description,
                                   double price, String category, String imageUrl) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.imageUrl = imageUrl;
        }

        @Override
        public String toString() {
            return name + " - €" + price;
        }
    }

    // Méthode utilitaire pour ajouter un produit à l'index (si nécessaire)
    public void addProductToIndex(ProductSearchResult product, final OnIndexingListener listener) {
        try {
            JSONObject record = new JSONObject();
            record.put("objectID", product.id);
            record.put("name", product.name);
            record.put("description", product.description);
            record.put("price", product.price);
            record.put("category", product.category);
            record.put("imageUrl", product.imageUrl);

            index.addObjectAsync(record, new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject content, AlgoliaException error) {
                    if (listener != null) {
                        if (error != null) {
                            listener.onIndexingError(error.getMessage());
                        } else {
                            listener.onIndexingSuccess();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            if (listener != null) {
                listener.onIndexingError("Erreur de création JSON: " + e.getMessage());
            }
        }
    }

    public interface OnIndexingListener {
        void onIndexingSuccess();
        void onIndexingError(String error);
    }
}