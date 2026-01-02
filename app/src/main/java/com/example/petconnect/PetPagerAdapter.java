package com.example.petconnect;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PetPagerAdapter extends RecyclerView.Adapter<PetPagerAdapter.PetViewHolder> {
    private static final String TAG = "PetPagerAdapter";
    private Context context;
    private List<Pet> petList;
    private Set<Integer> favorites = new HashSet<>();
    private ViewPager2 viewPager2;

    public PetPagerAdapter(Context context, List<Pet> petList, ViewPager2 viewPager2) {
        this.context = context;
        this.petList = petList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet_fullscreen, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);

        Log.d(TAG, "📌 Binding pet: " + pet.getName());
        Log.d(TAG, "📸 Photo URL: " + pet.getPhotoPath());

        // Données texte
        holder.petName.setText(pet.getName());
        holder.petAge.setText(pet.getAge());
        holder.petLocation.setText(pet.getLocation());

        // ✅ SOLUTION : FORCER LE CHARGEMENT DE L'IMAGE AVEC GLIDE
        String photoUrl = pet.getPhotoPath();

        // Réinitialiser la visibilité avant de charger
        holder.petEmoji.setVisibility(View.GONE);
        holder.petPhoto.setVisibility(View.GONE);

        if (photoUrl != null && !photoUrl.isEmpty()) {
            Log.d(TAG, "✅ Loading image from URL: " + photoUrl);

            // Afficher l'ImageView
            holder.petPhoto.setVisibility(View.VISIBLE);

            // Charger l'image avec Glide (configuration optimisée)
            Glide.with(context)
                    .load(photoUrl)
                    .placeholder(R.color.teal_primary)
                    .error(R.color.teal_primary)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .override(800, 800) // ⭐ Ajout: Limiter la taille pour éviter les problèmes de mémoire
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e,
                                                    Object model,
                                                    com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                                                    boolean isFirstResource) {
                            Log.e(TAG, "❌ Failed to load image: " + photoUrl);
                            if (e != null) {
                                Log.e(TAG, "Error: " + e.getMessage());
                            }
                            // En cas d'échec, afficher l'emoji
                            holder.petPhoto.post(() -> {
                                holder.petPhoto.setVisibility(View.GONE);
                                holder.petEmoji.setVisibility(View.VISIBLE);
                                holder.petEmoji.setText(pet.getEmoji());
                            });
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource,
                                                       Object model,
                                                       com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                                                       com.bumptech.glide.load.DataSource dataSource,
                                                       boolean isFirstResource) {
                            Log.d(TAG, "✅ Image loaded successfully: " + photoUrl);
                            return false;
                        }
                    })
                    .into(holder.petPhoto);

        } else {
            Log.d(TAG, "⚠️ No photo URL, showing emoji");

            // Pas d'URL → Afficher l'emoji
            holder.petEmoji.setVisibility(View.VISIBLE);
            holder.petEmoji.setText(pet.getEmoji());
        }

        // ✅ CLIC SUR TOUTE LA CARTE → PetDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PetDetailsActivity.class);
            intent.putExtra("pet", pet);
            context.startActivity(intent);
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Bouton MORE DETAILS
        if (holder.moreDetailsButton != null) {
            holder.moreDetailsButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, PetDetailsActivity.class);
                intent.putExtra("pet", pet);
                context.startActivity(intent);
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

        // 🆕 Bouton ADOPTER
        if (holder.adoptButton != null) {
            holder.adoptButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, AdoptionFormActivity.class);
                intent.putExtra("pet", pet);
                context.startActivity(intent);
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

        holder.petCard.setClickable(false);
        holder.petCard.setFocusable(false);

        // Favorite button
        holder.favoriteButton.setOnClickListener(v -> {
            v.setPressed(true);
            if (favorites.contains(pet.getId())) {
                favorites.remove(pet.getId());
                holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                favorites.add(pet.getId());
                holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
            }
        });

        // Animations pattes
        animatePawPrints(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    private void animatePawPrints(View itemView) {
        int[] pawIds = {R.id.paw1, R.id.paw2, R.id.paw3, R.id.paw4};
        Animation anim1 = AnimationUtils.loadAnimation(context, R.anim.paw_float);
        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.paw_float_slow);

        for (int i = 0; i < pawIds.length; i++) {
            TextView paw = itemView.findViewById(pawIds[i]);
            if (paw != null) {
                Animation selectedAnim = (i % 2 == 0) ? anim1 : anim2;
                paw.startAnimation(selectedAnim);
            }
        }
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        androidx.cardview.widget.CardView petCard;
        ImageView petPhoto, favoriteButton;
        TextView petEmoji, petName, petAge, petLocation;
        Button moreDetailsButton, adoptButton;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petPhoto = itemView.findViewById(R.id.petPhoto);
            petEmoji = itemView.findViewById(R.id.petEmoji);
            petName = itemView.findViewById(R.id.petName);
            petAge = itemView.findViewById(R.id.petAge);
            petLocation = itemView.findViewById(R.id.petLocation);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            petCard = itemView.findViewById(R.id.petCard);
            moreDetailsButton = itemView.findViewById(R.id.moreDetailsButton);

            // 🆕 Récupérer le bouton Adopter
            ViewGroup parent = (ViewGroup) itemView.findViewById(R.id.petInfoContainer);
            if (parent != null) {
                ViewGroup buttonsContainer = (ViewGroup) parent.getChildAt(parent.getChildCount() - 1);
                if (buttonsContainer instanceof android.widget.LinearLayout) {
                    if (buttonsContainer.getChildCount() >= 2) {
                        View secondChild = buttonsContainer.getChildAt(1);
                        if (secondChild instanceof Button) {
                            adoptButton = (Button) secondChild;
                        }
                    }
                }
            }
        }
    }
}