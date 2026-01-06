package com.example.petconnect.modules.event.repository;

import com.example.petconnect.modules.event.model.EventRegistration;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EventRegistrationRepository {
    private static final String COLLECTION_NAME = "event_registrations";
    private final FirebaseFirestore db;

    public EventRegistrationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // Récupérer toutes les inscriptions d'un événement
    public Task<QuerySnapshot> getRegistrationsByEventId(String eventId) {
        return db.collection(COLLECTION_NAME)
                .whereEqualTo("eventId", eventId)
                .get();
    }

    // Récupérer les inscriptions d'un utilisateur
    public Task<QuerySnapshot> getRegistrationsByUserId(String userId) {
        return db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get();
    }

    // Récupérer l'inscription d'un utilisateur pour un événement (doc id déterministe)
    public Task<DocumentSnapshot> getRegistrationByEventAndUser(String eventId, String userId) {
        return db.collection(COLLECTION_NAME)
                .document(buildRegistrationId(eventId, userId))
                .get();
    }

    // Ajouter une inscription
    public Task<Void> addRegistration(EventRegistration registration) {
        return db.collection(COLLECTION_NAME)
                .document(registration.getId())
                .set(registration);
    }

    // Annuler une inscription
    public Task<Void> cancelRegistration(String registrationId) {
        return db.collection(COLLECTION_NAME)
                .document(registrationId)
                .delete();
    }

    public String buildRegistrationId(String eventId, String userId) {
        return eventId + "_" + userId;
    }

    public DocumentReference eventDoc(String eventId) {
        return db.collection("events").document(eventId);
    }

    public DocumentReference registrationDoc(String registrationId) {
        return db.collection(COLLECTION_NAME).document(registrationId);
    }
}
