package com.example.petconnect_event.event.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.petconnect_event.R;
import com.example.petconnect_event.event.model.Event;
import com.example.petconnect_event.event.util.FavoritesManager;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventCardModernAdapter extends RecyclerView.Adapter<EventCardModernAdapter.ModernCardViewHolder> {
    private List<Event> events;
    private OnEventActionListener listener;
    private FavoritesManager favoritesManager;

    public interface OnEventActionListener {
        void onView(Event event);
        void onShare(Event event);
        void onFavoriteToggle(Event event);
    }

    public EventCardModernAdapter(List<Event> events, FavoritesManager favoritesManager) {
        this.events = events;
        this.favoritesManager = favoritesManager;
    }

    public void setOnEventActionListener(OnEventActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModernCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card_modern, parent, false);
        return new ModernCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModernCardViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, listener, favoritesManager);
        // Animer l'apparition de la carte
        animateCardIn(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    private void animateCardIn(View view) {
        AnimatorSet set = new AnimatorSet();
        
        ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        
        set.playTogether(translateY, alpha);
        set.setDuration(400);
        set.start();
    }

    public static class ModernCardViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleText;
        private TextView dateText;
        private TextView locationText;
        private TextView participantsText;
        private TextView statusBadge;
        private ImageView favoriteIcon;
        private MaterialButton viewButton;
        private MaterialButton shareButton;

        public ModernCardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewEvent);
            titleText = itemView.findViewById(R.id.textViewTitle);
            dateText = itemView.findViewById(R.id.textViewDate);
            locationText = itemView.findViewById(R.id.textViewLocation);
            participantsText = itemView.findViewById(R.id.textViewParticipants);
            statusBadge = itemView.findViewById(R.id.textViewStatusBadge);
            favoriteIcon = itemView.findViewById(R.id.imageViewFavorite);
            viewButton = itemView.findViewById(R.id.buttonView);
            shareButton = itemView.findViewById(R.id.buttonShare);
        }

        public void bind(Event event, OnEventActionListener listener, FavoritesManager favoritesManager) {
            if (event == null) return;

            // Title
            titleText.setText(event.getTitle() != null ? event.getTitle() : "Sans titre");

            // Date
            if (event.getEventDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
                dateText.setText(sdf.format(event.getEventDate()));
            }

            // Location
            locationText.setText(event.getLocation() != null ? event.getLocation() : "Lieu non défini");

            // Participants
            participantsText.setText(event.getCurrentParticipants() + "/" + event.getMaxParticipants());

            // Status Badge
            int currentParticipants = event.getCurrentParticipants();
            int maxParticipants = event.getMaxParticipants();
            
            if (maxParticipants > 0) {
                float capacityPercent = (currentParticipants / (float) maxParticipants) * 100;
                
                if (currentParticipants >= maxParticipants) {
                    statusBadge.setText("Complet");
                    statusBadge.setVisibility(View.VISIBLE);
                    statusBadge.setBackground(itemView.getContext().getDrawable(R.drawable.bg_badge_full));
                } else if (capacityPercent >= 75) {
                    statusBadge.setText("Bientôt complet");
                    statusBadge.setVisibility(View.VISIBLE);
                    statusBadge.setBackground(itemView.getContext().getDrawable(R.drawable.bg_badge_almost_full));
                } else {
                    statusBadge.setVisibility(View.GONE);
                }
            }

            // Image
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(event.getImageUrl())
                        .placeholder(R.drawable.ic_event)
                        .error(R.drawable.ic_event)
                        .centerCrop()
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_event);
            }

            // Favorite
            if (favoritesManager != null && event.getId() != null) {
                boolean isFav = favoritesManager.isFavorite(event.getId());
                favoriteIcon.setImageResource(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
                
                favoriteIcon.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onFavoriteToggle(event);
                        // Animer le clic
                        animateFavoriteClick(favoriteIcon);
                    }
                });
            }

            // Buttons
            viewButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onView(event);
                }
            });

            shareButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShare(event);
                }
            });
        }

        private void animateFavoriteClick(ImageView view) {
            AnimatorSet set = new AnimatorSet();
            
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f, 1f);
            
            set.playTogether(scaleX, scaleY);
            set.setDuration(300);
            set.start();
        }
    }
}
