package com.example.petconnect.modules.event.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.petconnect.R;
import com.example.petconnect.modules.event.model.Event;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView locationTextView;
    private ImageView eventImageView;
    private ImageButton moreButton;
    private ImageView favoriteIcon;
    private TextView statusBadgeTextView;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.textViewTitle);
        dateTextView = itemView.findViewById(R.id.textViewDate);
        locationTextView = itemView.findViewById(R.id.textViewLocation);
        eventImageView = itemView.findViewById(R.id.imageViewEvent);
        moreButton = itemView.findViewById(R.id.buttonMore);
        favoriteIcon = itemView.findViewById(R.id.imageViewFavorite);
        statusBadgeTextView = itemView.findViewById(R.id.textViewStatusBadge);
        
        // Vérifier que toutes les vues ont été trouvées
        if (titleTextView == null || dateTextView == null || locationTextView == null) {
            throw new IllegalStateException("Les vues du layout item_event.xml n'ont pas pu être trouvées");
        }
    }

    public void bind(Event event, EventAdapter.OnEventActionListener listener, com.example.petconnect.modules.event.util.FavoritesManager favoritesManager) {
        try {
            if (event == null) {
                return;
            }

            // Favorite icon
            if (favoriteIcon != null && favoritesManager != null && event.getId() != null) {
                boolean isFav = favoritesManager.isFavorite(event.getId());
                favoriteIcon.setImageResource(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
                favoriteIcon.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onFavoriteToggle(event);
                    }
                });
            }

            // Titre (avec valeur par défaut si null)
            if (titleTextView != null) {
                titleTextView.setText(event.getTitle() != null ? event.getTitle() : "Sans titre");
            }
            
            // Lieu (avec valeur par défaut si null)
            if (locationTextView != null) {
                locationTextView.setText(event.getLocation() != null ? event.getLocation() : "Lieu non défini");
            }
            
            // Formater la date (avec valeur par défaut si null)
            if (dateTextView != null) {
                if (event.getEventDate() != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
                        dateTextView.setText(sdf.format(event.getEventDate()));
                    } catch (Exception e) {
                        dateTextView.setText("Date non disponible");
                    }
                } else {
                    dateTextView.setText("Date non définie");
                }
            }

            // Gérer le clic
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onView(event);
                }
            });

            if (moreButton != null) {
                moreButton.setOnClickListener(v -> {
                    PopupMenu menu = new PopupMenu(itemView.getContext(), moreButton);
                    menu.inflate(R.menu.menu_event_item);
                    menu.setOnMenuItemClickListener(menuItem -> {
                        if (listener == null) return false;
                        int id = menuItem.getItemId();
                        if (id == R.id.action_edit_item) {
                            listener.onEdit(event);
                            return true;
                        } else if (id == R.id.action_delete_item) {
                            listener.onDelete(event);
                            return true;
                        } else if (id == R.id.action_share_item) {
                            listener.onShare(event);
                            return true;
                        }
                        return false;
                    });
                    menu.show();
                });
            }

            // Afficher le badge de statut basé sur les participants
            if (statusBadgeTextView != null) {
                int currentParticipants = event.getCurrentParticipants();
                int maxParticipants = event.getMaxParticipants();
                
                if (maxParticipants > 0) {
                    float capacityPercent = (currentParticipants / (float) maxParticipants) * 100;
                    
                    if (currentParticipants >= maxParticipants) {
                        // Complet
                        statusBadgeTextView.setText("Complet");
                        statusBadgeTextView.setVisibility(View.VISIBLE);
                        statusBadgeTextView.setBackground(itemView.getContext().getDrawable(R.drawable.bg_badge_full));
                    } else if (capacityPercent >= 75) {
                        // Bientôt complet (75% ou plus)
                        statusBadgeTextView.setText("Bientôt complet");
                        statusBadgeTextView.setVisibility(View.VISIBLE);
                        statusBadgeTextView.setBackground(itemView.getContext().getDrawable(R.drawable.bg_badge_almost_full));
                    } else {
                        statusBadgeTextView.setVisibility(View.GONE);
                    }
                } else {
                    statusBadgeTextView.setVisibility(View.GONE);
                }
            }

            // Charger l'image si disponible (avec Glide)
            if (eventImageView != null) {
                if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(event.getImageUrl())
                            .placeholder(R.drawable.ic_event)
                            .error(R.drawable.ic_event)
                            .into(eventImageView);
                } else {
                    eventImageView.setImageResource(R.drawable.ic_event);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("EventViewHolder", "Erreur dans bind: " + e.getMessage(), e);
        }
    }
}
