package com.example.petconnect.modules.shop.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.petconnect.R;
import com.example.petconnect.modules.shop.adapters.ProductAdapter;
import com.example.petconnect.modules.shop.models.Product;
import com.example.petconnect.modules.shop.services.CartService;
import com.example.petconnect.modules.shop.services.FirebaseService;
import com.example.petconnect.modules.shop.activities.ProductDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {
    private TextView cartBadge;
    private EditText searchInput;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private FirebaseService firebaseService;
    private BarChart barChartProducts;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            userId = "local_user";
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerProducts);
        cartBadge = findViewById(R.id.tv_cart_badge);
        searchInput = findViewById(R.id.et_search_products);
        barChartProducts = findViewById(R.id.line_chart_products);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(allProducts, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(ShopActivity.this, ProductDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            }

            @Override
            public void onAddToCart(Product product) {
                CartService.getInstance().add(product);
                updateBadge();
                CartService.getInstance().loadFromFirebase(new CartService.CartCallback() {
                    @Override
                    public void onSuccess() {
                        loadProductStatistics();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("ShopActivity", "Erreur rechargement panier: " + error);
                        loadProductStatistics();
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        findViewById(R.id.btn_cart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class))
        );

        View fabAddProduct = findViewById(R.id.fab_add_product);
        if (fabAddProduct != null) {
            fabAddProduct.setVisibility(View.GONE);
        }

        setupSearch();
        loadProducts();
        setupProductChart();
        loadProductStatistics();
        updateBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadge();
        loadProductStatistics();
    }

    private void updateBadge() {
        int cartCount = CartService.getInstance().getItemCount();
        cartBadge.setText(String.valueOf(cartCount));
        cartBadge.setVisibility(cartCount > 0 ? View.VISIBLE : View.GONE);
    }

    private void setupSearch() {
        if (searchInput == null) return;

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.updateProducts(new ArrayList<>(allProducts));
            return;
        }

        String searchQuery = query.trim().toLowerCase();
        List<Product> filtered = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getName() != null && product.getName().toLowerCase().contains(searchQuery)) {
                filtered.add(product);
            }
        }

        adapter.updateProducts(filtered);
    }

    private void loadProducts() {
        loadSampleProducts();

        firebaseService = new FirebaseService();
        firebaseService.getAllProducts(new FirebaseService.ProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> firebaseProducts) {
                if (firebaseProducts != null && !firebaseProducts.isEmpty()) {
                    for (Product firebaseProduct : firebaseProducts) {
                        boolean exists = false;
                        for (Product existingProduct : allProducts) {
                            if (existingProduct.getId().equals(firebaseProduct.getId())) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            allProducts.add(firebaseProduct);
                        }
                    }

                    adapter.updateProducts(new ArrayList<>(allProducts));
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void loadSampleProducts() {
        allProducts.clear();

        List<Product> sampleProducts = new ArrayList<>();

        sampleProducts.add(createProduct("1", "Croquettes Premium pour Chien", 29.99,
                "https://images.pexels.com/photos/2607544/pexels-photo-2607544.jpeg"));

        sampleProducts.add(createProduct("2", "Jouet Interactive pour Chat", 15.99,
                "https://images.pexels.com/photos/6853287/pexels-photo-6853287.jpeg"));

        sampleProducts.add(createProduct("3", "Litière Aglomérante 10L", 18.50,
                "https://images.pexels.com/photos/46024/pexels-photo-46024.jpeg"));

        sampleProducts.add(createProduct("4", "Harnais Ergonomique Taille M", 24.99,
                "https://images.pexels.com/photos/5518495/pexels-photo-5518495.jpeg"));

        sampleProducts.add(createProduct("5", "Gamelle Anti-Glouton", 12.99,
                "https://images.pexels.com/photos/15585218/pexels-photo-15585218.jpeg"));

        sampleProducts.add(createProduct("6", "Brosse à Poils Doux", 8.99,
                "https://images.pexels.com/photos/8088687/pexels-photo-8088687.jpeg"));

        sampleProducts.add(createProduct("7", "Os à Mâcher Dentaire", 9.99,
                "https://images.pexels.com/photos/1851164/pexels-photo-1851164.jpeg"));

        sampleProducts.add(createProduct("8", "Griffoir pour Chat", 22.99,
                "https://images.pexels.com/photos/821652/pexels-photo-821652.jpeg"));

        sampleProducts.add(createProduct("9", "Collier LED Sécurisé", 16.99,
                "https://images.pexels.com/photos/58997/pexels-photo-58997.jpeg"));

        sampleProducts.add(createProduct("10", "Shampooing Hypoallergénique", 14.50,
                "https://images.pexels.com/photos/3789885/pexels-photo-3789885.jpeg"));

        allProducts.addAll(sampleProducts);
        adapter.updateProducts(new ArrayList<>(allProducts));
    }

    private Product createProduct(String id, String name, double price, String imageUrl) {
        Product product = new Product(id, name, price, R.drawable.placeholder_product);
        product.setCategory("Animalerie");
        product.setDescription("Produit de qualité pour votre animal de compagnie.");
        product.setImageUrl(imageUrl);
        product.setStockQuantity(50);
        product.setRating(4.5);
        product.setAvailable(true);
        return product;
    }

    private void setupProductChart() {
        barChartProducts.getDescription().setEnabled(false);
        barChartProducts.setTouchEnabled(true);
        barChartProducts.setDragEnabled(true);
        barChartProducts.setScaleEnabled(true);
        barChartProducts.setPinchZoom(true);
        barChartProducts.setBackgroundColor(Color.WHITE);
        barChartProducts.getLegend().setEnabled(false);

        XAxis xAxis = barChartProducts.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        barChartProducts.getAxisLeft().setDrawGridLines(true);
        barChartProducts.getAxisRight().setEnabled(false);
    }

    private void loadProductStatistics() {
        CartService cartService = CartService.getInstance();
        cartService.loadFromFirebase(new CartService.CartCallback() {
            @Override
            public void onSuccess() {
                List<CartService.CartItem> cartItems = cartService.getItems();

                Map<String, Integer> productCounts = new HashMap<>();
                for (CartService.CartItem item : cartItems) {
                    String productName = item.product.getName();
                    if (productName != null) {
                        productCounts.put(productName, productCounts.getOrDefault(productName, 0) + item.quantity);
                    }
                }

                List<BarEntry> entries = new ArrayList<>();
                List<String> productNames = new ArrayList<>(productCounts.keySet());

                if (productNames.size() > 10) {
                    productNames = productNames.subList(0, 10);
                }

                for (int i = 0; i < productNames.size(); i++) {
                    String productName = productNames.get(i);
                    int count = productCounts.get(productName);
                    entries.add(new BarEntry(i, count));
                }

                if (entries.isEmpty()) {
                    entries.add(new BarEntry(0, 0));
                    entries.add(new BarEntry(1, 0));
                    entries.add(new BarEntry(2, 0));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Quantité ajoutée");
                dataSet.setColor(Color.parseColor("#FF6B35"));
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(10f);
                dataSet.setDrawValues(true);

                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.5f);
                barChartProducts.setData(barData);

                if (!productNames.isEmpty()) {
                    List<String> finalProductNames = productNames;
                    barChartProducts.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            int index = (int) value;
                            if (index >= 0 && index < finalProductNames.size()) {
                                String name = finalProductNames.get(index);
                                if (name.length() > 8) {
                                    return name.substring(0, 8) + "...";
                                }
                                return name;
                            }
                            return "";
                        }
                    });
                    barChartProducts.getXAxis().setLabelCount(Math.min(productNames.size(), 5), true);
                } else {
                    barChartProducts.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return "P" + ((int) value + 1);
                        }
                    });
                }

                barChartProducts.invalidate();
                barChartProducts.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Log.e("ShopActivity", "Erreur chargement statistiques: " + error);
            }
        });
    }
}
