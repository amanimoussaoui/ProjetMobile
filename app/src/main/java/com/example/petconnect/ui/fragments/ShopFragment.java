package com.example.petconnect.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.modules.shop.adapters.ProductAdapter;
import com.example.petconnect.modules.shop.models.Product;
import com.example.petconnect.shared.services.FirebaseService;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productsList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        
        productsRecyclerView = view.findViewById(R.id.products_recyclerview);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        productsList = new ArrayList<>();
        productAdapter = new ProductAdapter(productsList, this);
        productsRecyclerView.setAdapter(productAdapter);
        
        loadProducts();
        
        return view;
    }
    
    private void loadProducts() {
        FirebaseFirestore db = FirebaseService.getDb();
        db.collection("products")
            .addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    Toast.makeText(getContext(), "Erreur: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (snapshot != null) {
                    productsList.clear();
                    productsList.addAll(snapshot.toObjects(Product.class));
                    if (productsList.isEmpty()) {
                        productsList.addAll(getMockProducts());
                    }
                    productAdapter.updateProducts(productsList);
                }
            });
    }

    private List<Product> getMockProducts() {
        Product croquettes = new Product("1", "Croquettes Premium", 24.99, R.drawable.placeholder_product);
        croquettes.setDescription("Riche en protéines.");
        croquettes.setRating(4.5);

        Product jouet = new Product("2", "Jouet à mâcher", 9.99, R.drawable.placeholder_product);
        jouet.setDescription("Résistant et non toxique.");
        jouet.setRating(4.2);

        Product laisse = new Product("3", "Laisse réglable", 14.50, R.drawable.placeholder_product);
        laisse.setDescription("Confortable pour les promenades.");
        laisse.setRating(4.7);

        return Arrays.asList(croquettes, jouet, laisse);
    }
    
    @Override
    public void onProductClick(Product product) {
        Toast.makeText(getContext(), "Produit: " + product.getName(),
            Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToCart(Product product) {
        Toast.makeText(getContext(), "Ajouté au panier: " + product.getName(),
            Toast.LENGTH_SHORT).show();
    }
}
