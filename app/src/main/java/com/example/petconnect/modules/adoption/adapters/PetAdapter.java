package com.example.petconnect.modules.adoption.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.shared.models.Pet;
import com.example.petconnect.databinding.ItemPetBinding;
import com.bumptech.glide.Glide;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> pets;
    private OnPetClickListener listener;
    
    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }
    
    public PetAdapter(List<Pet> pets, OnPetClickListener listener) {
        this.pets = pets;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPetBinding binding = ItemPetBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new PetViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.bind(pet, listener);
    }
    
    @Override
    public int getItemCount() {
        return pets != null ? pets.size() : 0;
    }
    
    public void updatePets(List<Pet> newPets) {
        this.pets = newPets;
        notifyDataSetChanged();
    }
    
    static class PetViewHolder extends RecyclerView.ViewHolder {
        private ItemPetBinding binding;
        
        public PetViewHolder(ItemPetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        public void bind(Pet pet, OnPetClickListener listener) {
            binding.petName.setText(pet.name);
            binding.petBreed.setText(pet.breed);
            binding.petType.setText(pet.type);
            binding.petAge.setText("Age: " + pet.age);
            binding.petDescription.setText(pet.description);
            
            if (pet.imageUrl != null && !pet.imageUrl.isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                    .load(pet.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.petImage);
            }
            
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPetClick(pet);
                }
            });
        }
    }
}
