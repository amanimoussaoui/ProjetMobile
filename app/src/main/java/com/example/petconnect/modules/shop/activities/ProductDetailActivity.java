package com.example.petconnect.modules.shop.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.petconnect.R;
import com.example.petconnect.modules.shop.models.Product;
import com.example.petconnect.modules.shop.services.CartService;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvName, tvPrice, tvCategory, tvDescription;
    private Button btnAddToCart;
    private ImageView productImage;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initializeViews();

        currentProduct = (Product) getIntent().getSerializableExtra("product");

        if (currentProduct != null) {
            loadProductData();
        } else {
            Toast.makeText(this, "Produit non disponible", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        tvName = findViewById(R.id.tv_product_name);
        tvPrice = findViewById(R.id.tv_product_price);
        tvCategory = findViewById(R.id.tv_product_category);
        tvDescription = findViewById(R.id.tv_product_description);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        productImage = findViewById(R.id.product_image);

        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void loadProductData() {
        if (currentProduct == null) return;

        tvName.setText(currentProduct.getName());
        tvPrice.setText(String.format("Prix : €%.2f", currentProduct.getPrice()));
        tvCategory.setText(currentProduct.getCategory());
        tvDescription.setText(currentProduct.getDescription());
    }

    private void addToCart() {
        if (currentProduct == null) {
            Toast.makeText(this, "Produit non disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        CartService cartService = CartService.getInstance();
        cartService.add(currentProduct);

        Toast.makeText(this, currentProduct.getName() + " ajouté au panier", Toast.LENGTH_SHORT).show();
    }
}
