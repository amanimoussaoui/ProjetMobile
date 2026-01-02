package com.example.petconnect_event.event.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect_event.R;
import com.example.petconnect_event.event.model.Event;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private List<Event> events;
    private final OnEventActionListener listener;
    private final com.example.petconnect_event.event.util.FavoritesManager favoritesManager;

    // Interface de clics
    public interface OnEventActionListener {
        void onView(Event event);
        void onEdit(Event event);
        void onDelete(Event event);
        void onFavoriteToggle(Event event);
        void onShare(Event event);
    }

    public EventAdapter(List<Event> events, OnEventActionListener listener, com.example.petconnect_event.event.util.FavoritesManager favoritesManager) {
        this.events = events;
        this.listener = listener;
        this.favoritesManager = favoritesManager;
    }

    public void updateData(List<Event> newEvents) {
        this.events = newEvents != null ? newEvents : java.util.Collections.emptyList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, listener, favoritesManager);
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }
}
