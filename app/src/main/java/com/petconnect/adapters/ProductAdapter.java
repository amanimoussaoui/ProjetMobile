package com.petconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.petconnect.R;
import com.petconnect.activities.ProductDetailActivity;
import com.petconnect.models.Product;
import com.petconnect.services.CartService;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private final Runnable onCartUpdate;
    private Context context;

    public ProductAdapter(List<Product> products, Runnable onCartUpdate) {
        this.products = products;
        this.onCartUpdate = onCartUpdate;
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

        // Charger l'image avec Glide - CORRECTION PRINCIPALE
        String imageUrl = product.getImageUrlString();

        // Options pour préserver les couleurs
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .dontTransform() // Évite les transformations de couleur
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache optimisé
                .centerCrop();

        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null")) {
            // Charger depuis URL
            Glide.with(context)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(holder.image);
        } else {
            // Utiliser l'image locale
            int imageRes = product.getImageRes();
            if (imageRes != 0) {
                holder.image.setImageResource(imageRes);
            } else {
                holder.image.setImageResource(R.drawable.placeholder_product);
            }
        }

        // Bouton ajouter au panier
        holder.add.setOnClickListener(v -> {
            CartService.getInstance().add(product);
            onCartUpdate.run();
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
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        Button add;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_product_name);
            price = itemView.findViewById(R.id.tv_product_price);
            add = itemView.findViewById(R.id.btn_add_to_cart);
            image = itemView.findViewById(R.id.product_image);

            // Assurer que l'ImageView n'a pas de tint
            image.setColorFilter(null);
        }
    }
}