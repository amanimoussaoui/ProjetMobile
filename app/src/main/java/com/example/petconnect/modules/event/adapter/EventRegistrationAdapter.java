package com.example.petconnect.modules.event.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.modules.event.model.EventRegistration;
import java.util.ArrayList;
import java.util.List;

public class EventRegistrationAdapter extends RecyclerView.Adapter<EventRegistrationAdapter.RegistrationViewHolder> {
    private List<EventRegistration> registrationList = new ArrayList<>();

    public void setRegistrationList(List<EventRegistration> registrations) {
        this.registrationList = registrations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new RegistrationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationViewHolder holder, int position) {
        EventRegistration registration = registrationList.get(position);
        holder.bind(registration);
    }

    @Override
    public int getItemCount() {
        return registrationList.size();
    }

    class RegistrationViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView emailTextView;

        public RegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(android.R.id.text1);
            emailTextView = itemView.findViewById(android.R.id.text2);
        }

        public void bind(EventRegistration registration) {
            nameTextView.setText(registration.getUserName());
            emailTextView.setText(registration.getUserEmail());
        }
    }
}
