package com.example.petconnect.modules.event.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.shared.models.Event;
import com.example.petconnect.databinding.ItemEventBinding;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;
    private OnEventClickListener listener;
    
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
    
    public EventAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventBinding binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, listener);
    }
    
    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }
    
    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }
    
    static class EventViewHolder extends RecyclerView.ViewHolder {
        private ItemEventBinding binding;
        
        public EventViewHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(Event event, OnEventClickListener listener) {
            binding.textViewTitle.setText(event.title);
            binding.textViewLocation.setText(event.location);
            
            if (event.eventDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
                binding.textViewDate.setText(sdf.format(event.eventDate.toDate()));
            }
            
            if (event.imageUrl != null && !event.imageUrl.isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                    .load(event.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imageViewEvent);
            }
            
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        }
    }
}
