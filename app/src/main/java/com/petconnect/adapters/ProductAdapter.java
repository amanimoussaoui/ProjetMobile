package com.petconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.petconnect.R;
import com.petconnect.activities.ProductDetailActivity;
import com.petconnect.models.Product;
import com.petconnect.services.CartService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private final Runnable onCartUpdate;
    private Context context;
    private Map<String, Integer> productPopularity = new HashMap<>();

    public ProductAdapter(List<Product> products, Runnable onCartUpdate) {
        this.products = products;
        this.onCartUpdate = onCartUpdate;
        loadProductPopularity();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        // Configurer les textes
        holder.name.setText(product.getName());
        holder.price.setText(String.format("%.2f €", product.getFinalPrice()));

        // Charger l'image avec Glide
        String imageUrl = product.getImageUrlString();
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null")) {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(holder.image);
        } else {
            int imageRes = product.getImageRes();
            if (imageRes != 0) {
                holder.image.setImageResource(imageRes);
            } else {
                holder.image.setImageResource(R.drawable.placeholder_product);
            }
        }

        // Calculer et afficher les étoiles de popularité
        int popularityStars = calculatePopularityStars(product.getId());
        displayStars(holder.starsContainer, popularityStars);

        // Bouton ajouter au panier
        holder.add.setOnClickListener(v -> {
            CartService.getInstance().add(product);
            onCartUpdate.run();
            // Mettre à jour la popularité après l'ajout
            updateProductPopularity(product.getId());
            notifyItemChanged(position);
        });

        // Clic sur l'item pour voir les détails
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        loadProductPopularity();
        notifyDataSetChanged();
    }

    private void loadProductPopularity() {
        CartService cartService = CartService.getInstance();
        List<CartService.CartItem> cartItems = cartService.getItems();

        // Réinitialiser la popularité
        productPopularity.clear();

        // Compter les ajouts pour chaque produit
        for (CartService.CartItem item : cartItems) {
            String productId = item.product.getId();
            int currentCount = productPopularity.getOrDefault(productId, 0);
            productPopularity.put(productId, currentCount + item.quantity);
        }
    }

    private void updateProductPopularity(String productId) {
        int currentPopularity = productPopularity.getOrDefault(productId, 0);
        productPopularity.put(productId, currentPopularity + 1);
    }

    private int calculatePopularityStars(String productId) {
        int popularityCount = productPopularity.getOrDefault(productId, 0);

        // Trouver la popularité maximale parmi tous les produits
        int maxPopularity = 0;
        for (int count : productPopularity.values()) {
            if (count > maxPopularity) {
                maxPopularity = count;
            }
        }

        // Si aucun produit n'a été ajouté, retourner 0 étoiles
        if (maxPopularity == 0) {
            return 0;
        }

        // Calculer le nombre d'étoiles (0 à 6)
        // Formule: (popularité du produit / popularité max) * 6
        float ratio = (float) popularityCount / maxPopularity;
        int stars = Math.round(ratio * 6);

        // S'assurer que c'est entre 0 et 6
        return Math.min(Math.max(stars, 0), 6);
    }

    private void displayStars(LinearLayout starsContainer, int filledStars) {
        // Vider le conteneur d'étoiles
        starsContainer.removeAllViews();

        // Créer 6 étoiles
        for (int i = 0; i < 6; i++) {
            ImageView star = new ImageView(context);

            // Définir la taille
            int starSize = (int) context.getResources().getDimension(R.dimen.star_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(starSize, starSize);
            params.setMargins(2, 0, 2, 0); // Petit espace entre les étoiles
            star.setLayoutParams(params);

            // Choisir l'image de l'étoile
            if (i < filledStars) {
                // Étoile colorée (pleine)
                star.setImageResource(R.drawable.ic_star_filled);
                star.setColorFilter(ContextCompat.getColor(context, R.color.star_color),
                        PorterDuff.Mode.SRC_IN);
            } else {
                // Étoile vide (non colorée)
                star.setImageResource(R.drawable.ic_star_outline);
                star.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            }

            // Ajouter l'étoile au conteneur
            starsContainer.addView(star);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        Button add;
        ImageView image;
        LinearLayout starsContainer;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_product_name);
            price = itemView.findViewById(R.id.tv_product_price);
            add = itemView.findViewById(R.id.btn_add_to_cart);
            image = itemView.findViewById(R.id.product_image);
            starsContainer = itemView.findViewById(R.id.ll_stars);

            // Assurer que l'ImageView n'a pas de tint
            image.setColorFilter(null);
        }
    }
}