package com.example.petconnect_event.event.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class FavoritesManager {
    private static final String PREFS_NAME = "event_favorites";
    private static final String KEY_FAVORITES = "favorite_event_ids";
    
    private final SharedPreferences prefs;
    
    public FavoritesManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public boolean isFavorite(String eventId) {
        return getFavorites().contains(eventId);
    }
    
    public void addFavorite(String eventId) {
        Set<String> favorites = getFavorites();
        favorites.add(eventId);
        saveFavorites(favorites);
    }
    
    public void removeFavorite(String eventId) {
        Set<String> favorites = getFavorites();
        favorites.remove(eventId);
        saveFavorites(favorites);
    }
    
    public void toggleFavorite(String eventId) {
        if (isFavorite(eventId)) {
            removeFavorite(eventId);
        } else {
            addFavorite(eventId);
        }
    }
    
    public Set<String> getFavorites() {
        return new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
    }
    
    private void saveFavorites(Set<String> favorites) {
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }
}
