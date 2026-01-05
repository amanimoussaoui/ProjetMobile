package com.petconnect.services;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAnalyticsService {
    private static final String TAG = "ProductAnalyticsService";
    private FirebaseFirestore db;

    public ProductAnalyticsService() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface AnalyticsCallback {
        void onAnalyticsLoaded(Map<String, Double> salesByProduct);
        void onError(String error);
    }

    public interface TopProductsCallback {
        void onTopProductsLoaded(List<ProductSales> topProducts);
        void onError(String error);
    }

    // Classe pour représenter les ventes par produit
    public static class ProductSales {
        private String productId;
        private String productName;
        private int quantitySold;
        private double totalRevenue;

        public ProductSales(String productId, String productName, int quantitySold, double totalRevenue) {
            this.productId = productId;
            this.productName = productName;
            this.quantitySold = quantitySold;
            this.totalRevenue = totalRevenue;
        }

        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantitySold() { return quantitySold; }
        public double getTotalRevenue() { return totalRevenue; }

        public void setProductName(String productName) { this.productName = productName; }
        public void addSale(int quantity, double price) {
            this.quantitySold += quantity;
            this.totalRevenue += (quantity * price);
        }

        @Override
        public String toString() {
            return productName + " - Ventes: " + quantitySold + " - CA: " + String.format("%.2f €", totalRevenue);
        }
    }

    // Obtenir les statistiques de vente par produit
    public void getSalesByProduct(AnalyticsCallback callback) {
        db.collection("orders")
                .whereEqualTo("status", "paid")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Double> salesMap = new HashMap<>();

                    for (QueryDocumentSnapshot orderDoc : queryDocumentSnapshots) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) orderDoc.get("items");
                        if (items != null) {
                            for (Map<String, Object> item : items) {
                                String productId = (String) item.get("productId");
                                Double quantity = ((Long) item.get("quantity")).doubleValue();
                                Double price = (Double) item.get("price");

                                if (productId != null && quantity != null && price != null) {
                                    double itemTotal = quantity * price;
                                    salesMap.put(productId, salesMap.getOrDefault(productId, 0.0) + itemTotal);
                                }
                            }
                        }
                    }

                    callback.onAnalyticsLoaded(salesMap);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur chargement stats: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    // Obtenir les produits les plus vendus
    public void getTopSellingProducts(int limit, TopProductsCallback callback) {
        db.collection("orders")
                .whereEqualTo("status", "paid")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, ProductSales> productSalesMap = new HashMap<>();

                    for (QueryDocumentSnapshot orderDoc : queryDocumentSnapshots) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) orderDoc.get("items");
                        if (items != null) {
                            for (Map<String, Object> item : items) {
                                String productId = (String) item.get("productId");
                                String productName = (String) item.get("productName");
                                Long quantityLong = (Long) item.get("quantity");
                                Double price = (Double) item.get("price");

                                if (productId != null && quantityLong != null && price != null) {
                                    int quantity = quantityLong.intValue();

                                    if (!productSalesMap.containsKey(productId)) {
                                        productSalesMap.put(productId, new ProductSales(productId,
                                                productName != null ? productName : "Produit " + productId,
                                                0, 0.0));
                                    }

                                    ProductSales sales = productSalesMap.get(productId);
                                    sales.addSale(quantity, price);
                                }
                            }
                        }
                    }

                    // Convertir en liste et trier par quantité vendue
                    List<ProductSales> topProducts = new ArrayList<>(productSalesMap.values());
                    topProducts.sort((p1, p2) -> Integer.compare(p2.getQuantitySold(), p1.getQuantitySold()));

                    // Limiter au nombre demandé
                    if (limit > 0 && topProducts.size() > limit) {
                        topProducts = topProducts.subList(0, limit);
                    }

                    callback.onTopProductsLoaded(topProducts);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

    // Obtenir le taux de vente par produit (pourcentage des ventes totales)
    public void getSalesPercentageByProduct(AnalyticsCallback callback) {
        getSalesByProduct(new AnalyticsCallback() {
            @Override
            public void onAnalyticsLoaded(Map<String, Double> salesByProduct) {
                double totalSales = 0;
                for (Double sales : salesByProduct.values()) {
                    totalSales += sales;
                }

                // Calculer les pourcentages
                Map<String, Double> percentageMap = new HashMap<>();
                for (Map.Entry<String, Double> entry : salesByProduct.entrySet()) {
                    double percentage = totalSales > 0 ? (entry.getValue() / totalSales) * 100 : 0;
                    percentageMap.put(entry.getKey(), percentage);
                }

                callback.onAnalyticsLoaded(percentageMap);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
}