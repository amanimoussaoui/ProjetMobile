package com.petconnect.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.petconnect.R;
import com.petconnect.models.Product;
import com.petconnect.services.CartService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvName, tvPrice, tvCategory, tvDescription;
    private Button btnAddToCart;
    private ImageView productImage;
    private LinearLayout starsContainer;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialiser les vues
        initializeViews();

        // Récupérer le produit depuis l'intent
        currentProduct = (Product) getIntent().getSerializableExtra("product");

        if (currentProduct != null) {
            loadProductData();
            loadProductPopularity();
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
        starsContainer = findViewById(R.id.ll_stars_detail);

        // Bouton Ajouter au panier
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void loadProductData() {
        if (currentProduct == null) return;

        tvName.setText(currentProduct.getName());
        tvPrice.setText(String.format("Prix : €%.2f", currentProduct.getPrice()));
        tvCategory.setText(currentProduct.getCategory());
        tvDescription.setText(currentProduct.getDescription());

        // Charger l'image
        String imageUrl = currentProduct.getImageUrlString();
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null")) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .centerCrop()
                    .into(productImage);
        }
    }

    private void loadProductPopularity() {
        CartService cartService = CartService.getInstance();
        List<CartService.CartItem> cartItems = cartService.getItems();

        // Compter les ajouts pour chaque produit
        Map<String, Integer> productCounts = new HashMap<>();
        for (CartService.CartItem item : cartItems) {
            String productId = item.product.getId();
            productCounts.put(productId, productCounts.getOrDefault(productId, 0) + item.quantity);
        }

        // Calculer les étoiles pour ce produit
        int popularityStars = calculatePopularityStars(currentProduct.getId(), productCounts);
        displayStars(popularityStars);
    }

    private int calculatePopularityStars(String productId, Map<String, Integer> productCounts) {
        int popularityCount = productCounts.getOrDefault(productId, 0);

        // Trouver la popularité maximale parmi tous les produits
        int maxPopularity = 0;
        for (int count : productCounts.values()) {
            if (count > maxPopularity) {
                maxPopularity = count;
            }
        }

        // Si aucun produit n'a été ajouté, retourner 0 étoiles
        if (maxPopularity == 0) {
            return 0;
        }

        // Calculer le nombre d'étoiles (0 à 6)
        float ratio = (float) popularityCount / maxPopularity;
        int stars = Math.round(ratio * 6);

        // S'assurer que c'est entre 0 et 6
        return Math.min(Math.max(stars, 0), 6);
    }

    private void displayStars(int filledStars) {
        if (starsContainer == null) return;

        starsContainer.removeAllViews();

        for (int i = 0; i < 6; i++) {
            ImageView star = new ImageView(this);
            int starSize = (int) getResources().getDimension(R.dimen.star_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(starSize, starSize);
            params.setMargins(2, 0, 2, 0);
            star.setLayoutParams(params);

            if (i < filledStars) {
                star.setImageResource(R.drawable.ic_star_filled);
                star.setColorFilter(ContextCompat.getColor(this, R.color.star_color), PorterDuff.Mode.SRC_IN);
            } else {
                star.setImageResource(R.drawable.ic_star_outline);
                star.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            }

            starsContainer.addView(star);
        }
    }

    private void addToCart() {
        if (currentProduct == null) {
            Toast.makeText(this, "Produit non disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        CartService cartService = CartService.getInstance();

        // Ajouter au panier
        cartService.add(currentProduct);

        // Recalculer les étoiles après l'ajout
        loadProductPopularity();

        Toast.makeText(this, currentProduct.getName() + " ajouté au panier", Toast.LENGTH_SHORT).show();
    }
}