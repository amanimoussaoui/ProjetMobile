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
import com.example.petconnect.modules.event.adapters.EventAdapter;
import com.example.petconnect.shared.models.Event;
import com.example.petconnect.shared.services.FirebaseService;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventsFragment extends Fragment implements EventAdapter.OnEventClickListener {
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventsList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        
        eventsRecyclerView = view.findViewById(R.id.events_recyclerview);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        eventsList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventsList, this);
        eventsRecyclerView.setAdapter(eventAdapter);
        
        loadEvents();
        
        return view;
    }
    
    private void loadEvents() {
        FirebaseFirestore db = FirebaseService.getDb();
        db.collection("events")
            .addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    Toast.makeText(getContext(), "Erreur: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (snapshot != null) {
                    eventsList.clear();
                    eventsList.addAll(snapshot.toObjects(Event.class));
                    if (eventsList.isEmpty()) {
                        eventsList.addAll(getMockEvents());
                    }
                    eventAdapter.updateEvents(eventsList);
                }
            });
    }

    private List<Event> getMockEvents() {
        return Arrays.asList(
            new Event(
                "Atelier bien-être animal",
                "Découverte des soins de base et conseils vétérinaires.",
                "Paris",
                Timestamp.now(),
                "",
                "org1",
                30
            ),
            new Event(
                "Promenade collective",
                "Balade conviviale avec d'autres propriétaires.",
                "Lyon",
                Timestamp.now(),
                "",
                "org2",
                20
            ),
            new Event(
                "Journée d'adoption",
                "Rencontrez des animaux prêts à être adoptés.",
                "Marseille",
                Timestamp.now(),
                "",
                "org3",
                25
            )
        );
    }
    
    @Override
    public void onEventClick(Event event) {
        Toast.makeText(getContext(), "Événement: " + event.title, 
            Toast.LENGTH_SHORT).show();
    }
}
