package com.example.petconnect.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.modules.adoption.adapters.PetAdapter;
import com.example.petconnect.shared.models.Pet;
import com.example.petconnect.shared.services.FirebaseService;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdoptionFragment extends Fragment implements PetAdapter.OnPetClickListener {
    private RecyclerView petsRecyclerView;
    private PetAdapter petAdapter;
    private List<Pet> petsList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adoption, container, false);
        
        petsRecyclerView = view.findViewById(R.id.pets_recyclerview);
        petsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        petsList = new ArrayList<>();
        petAdapter = new PetAdapter(petsList, this);
        petsRecyclerView.setAdapter(petAdapter);
        
        loadPets();
        
        return view;
    }
    
    private void loadPets() {
        FirebaseFirestore db = FirebaseService.getDb();
        db.collection("pets")
            .addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    Toast.makeText(getContext(), "Erreur: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (snapshot != null) {
                    petsList.clear();
                    petsList.addAll(snapshot.toObjects(Pet.class));
                    if (petsList.isEmpty()) {
                        petsList.addAll(getMockPets());
                    }
                    petAdapter.updatePets(petsList);
                }
            });
    }

    private List<Pet> getMockPets() {
        Pet luna = new Pet("Luna", "Berger", "Chien", "2 ans",
            "Joueuse et affectueuse.", "", "Bordeaux");
        luna.isAvailable = true;
        luna.createdAt = Timestamp.now();

        Pet mimi = new Pet("Mimi", "Européen", "Chat", "1 an",
            "Calme, aime les câlins.", "", "Toulouse");
        mimi.isAvailable = true;
        mimi.createdAt = Timestamp.now();

        Pet rocky = new Pet("Rocky", "Labrador", "Chien", "3 ans",
            "Énergique, idéal famille.", "", "Lille");
        rocky.isAvailable = true;
        rocky.createdAt = Timestamp.now();

        return Arrays.asList(luna, mimi, rocky);
    }
    
    @Override
    public void onPetClick(Pet pet) {
        Toast.makeText(getContext(), "Animal: " + pet.name, 
            Toast.LENGTH_SHORT).show();
    }
}
