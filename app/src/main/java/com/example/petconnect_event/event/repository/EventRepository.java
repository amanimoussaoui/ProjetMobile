package com.example.petconnect_event.event.repository;

import com.example.petconnect_event.event.model.Event;
import com.example.petconnect_event.event.model.EventRegistration;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRepository {
    private static final String COLLECTION_NAME = "events";
    private final FirebaseFirestore db;

    public EventRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Interface callback
    public interface OnEventsLoadedListener {
        void onLoaded(List<Event> events);
    }

    public interface OnEventLoadedListener {
        void onLoaded(Event event);
        void onError(String error);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onError(String error);
    }

    // Récupérer tous les événements (tolérant aux anciennes données string pour eventDate)
    public void getAllEvents(OnEventsLoadedListener listener) {
        db.collection(COLLECTION_NAME)
                // orderBy retiré pour éviter l'échec si les types eventDate sont mixtes
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        try {
                            Event event = doc.toObject(Event.class);
                            if (event != null) {
                                event.setId(doc.getId());
                                events.add(event);
                                continue;
                            }
                        } catch (Exception e) {
                            android.util.Log.w("EventRepository", "Standard mapping failed, trying manual parse: " + e.getMessage());
                        }

                        // Fallback manuel pour anciens documents avec date en String
                        try {
                            Event fallback = new Event();
                            fallback.setId(doc.getId());
                            fallback.setTitle(doc.getString("title"));
                            fallback.setDescription(doc.getString("description"));
                            fallback.setLocation(doc.getString("location"));
                            fallback.setImageUrl(doc.getString("imageUrl"));
                            fallback.setOrganizerId(doc.getString("organizerId"));
                            Long maxP = doc.getLong("maxParticipants");
                            Long curP = doc.getLong("currentParticipants");
                            fallback.setMaxParticipants(maxP != null ? maxP.intValue() : 0);
                            fallback.setCurrentParticipants(curP != null ? curP.intValue() : 0);
                            fallback.setActive(Boolean.TRUE.equals(doc.getBoolean("active")) || Boolean.TRUE.equals(doc.getBoolean("isActive")));

                            Object dateObj = doc.get("eventDate");
                            if (dateObj instanceof com.google.firebase.Timestamp) {
                                com.google.firebase.Timestamp ts = (com.google.firebase.Timestamp) dateObj;
                                fallback.setEventDate(ts.toDate());
                            } else if (dateObj instanceof String) {
                                fallback.setEventDate((String) dateObj);
                            }

                            Object createdObj = doc.get("createdAt");
                            if (createdObj instanceof com.google.firebase.Timestamp) {
                                com.google.firebase.Timestamp tsC = (com.google.firebase.Timestamp) createdObj;
                                fallback.setCreatedAt(tsC.toDate());
                            } else if (createdObj instanceof String) {
                                fallback.setCreatedAt((String) createdObj);
                            }

                            events.add(fallback);
                        } catch (Exception ex) {
                            android.util.Log.e("EventRepository", "Manual parse failed: " + ex.getMessage(), ex);
                        }
                    }
                    listener.onLoaded(events);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("EventRepository", "Error fetching events: " + e.getMessage(), e);
                    listener.onLoaded(new ArrayList<>());
                });
    }

    // Récupérer un événement par ID
    public void getEventById(String eventId, OnEventLoadedListener listener) {
        db.collection(COLLECTION_NAME)
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        listener.onError("Événement n'existe pas");
                        return;
                    }

                    try {
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            event.setId(documentSnapshot.getId());
                            listener.onLoaded(event);
                            return;
                        }
                    } catch (Exception e) {
                        android.util.Log.w("EventRepository", "Standard mapping failed in getEventById: " + e.getMessage());
                    }

                    // Fallback manuel pour les documents anciens (dates en String)
                    try {
                        Event fallback = new Event();
                        fallback.setId(documentSnapshot.getId());
                        fallback.setTitle(documentSnapshot.getString("title"));
                        fallback.setDescription(documentSnapshot.getString("description"));
                        fallback.setLocation(documentSnapshot.getString("location"));
                        fallback.setImageUrl(documentSnapshot.getString("imageUrl"));
                        fallback.setOrganizerId(documentSnapshot.getString("organizerId"));
                        Long maxP = documentSnapshot.getLong("maxParticipants");
                        Long curP = documentSnapshot.getLong("currentParticipants");
                        fallback.setMaxParticipants(maxP != null ? maxP.intValue() : 0);
                        fallback.setCurrentParticipants(curP != null ? curP.intValue() : 0);
                        fallback.setActive(Boolean.TRUE.equals(documentSnapshot.getBoolean("active")) || Boolean.TRUE.equals(documentSnapshot.getBoolean("isActive")));

                        Object dateObj = documentSnapshot.get("eventDate");
                        if (dateObj instanceof com.google.firebase.Timestamp) {
                            com.google.firebase.Timestamp ts = (com.google.firebase.Timestamp) dateObj;
                            fallback.setEventDate(ts.toDate());
                        } else if (dateObj instanceof String) {
                            fallback.setEventDate((String) dateObj);
                        }

                        Object createdObj = documentSnapshot.get("createdAt");
                        if (createdObj instanceof com.google.firebase.Timestamp) {
                            com.google.firebase.Timestamp tsC = (com.google.firebase.Timestamp) createdObj;
                            fallback.setCreatedAt(tsC.toDate());
                        } else if (createdObj instanceof String) {
                            fallback.setCreatedAt((String) createdObj);
                        }

                        listener.onLoaded(fallback);
                    } catch (Exception ex) {
                        android.util.Log.e("EventRepository", "Manual parse failed in getEventById: " + ex.getMessage(), ex);
                        listener.onError("Échec du chargement de l'événement");
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // Ajouter un événement
    public void addEvent(Event event, OnOperationCompleteListener listener) {
        db.collection(COLLECTION_NAME)
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    event.setId(documentReference.getId());
                    android.util.Log.d("EventRepository", "addEvent success id=" + documentReference.getId());
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("EventRepository", "addEvent failed: " + e.getMessage(), e);
                    listener.onError(e.getMessage());
                });
    }

    // Mettre à jour un événement
    public void updateEvent(Event event, OnOperationCompleteListener listener) {
        if (event.getId() == null || event.getId().isEmpty()) {
            listener.onError("ID d'événement invalide");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", event.getTitle());
        updates.put("description", event.getDescription());
        updates.put("location", event.getLocation());
        updates.put("eventDate", event.getEventDate());
        updates.put("maxParticipants", event.getMaxParticipants());
        updates.put("imageUrl", event.getImageUrl());

        db.collection(COLLECTION_NAME)
                .document(event.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // Supprimer un événement
    public void deleteEvent(String eventId, OnOperationCompleteListener listener) {
        db.collection(COLLECTION_NAME)
                .document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // Mettre à jour le nombre de participants
    public void updateParticipantCount(String eventId, int newCount, OnOperationCompleteListener listener) {
        db.collection(COLLECTION_NAME)
                .document(eventId)
                .update("currentParticipants", newCount)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // Inscription avec quota (transaction): vĂ©rifie le quota, incrĂŠmente et crĂŠe l'inscription
    public Task<Void> registerWithQuota(EventRegistration registration) {
        String eventId = registration.getEventId();
        String regId = registration.getId();
        return db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnap = transaction.get(db.collection(COLLECTION_NAME).document(eventId));
            if (!eventSnap.exists()) {
                throw new IllegalStateException("L'événement n'existe pas");
            }

            DocumentSnapshot regSnap = transaction.get(db.collection("event_registrations").document(regId));
            if (regSnap.exists()) {
                // DĂ©jĂ  inscrit, ne pas doubler ni incrementer
                return null;
            }

            Long maxP = eventSnap.getLong("maxParticipants");
            Long curP = eventSnap.getLong("currentParticipants");
            int max = maxP != null ? maxP.intValue() : 0;
            int cur = curP != null ? curP.intValue() : 0;

            if (max > 0 && cur >= max) {
                throw new IllegalStateException("Événement complet");
            }

            transaction.update(eventSnap.getReference(), "currentParticipants", cur + 1);
            transaction.set(db.collection("event_registrations").document(regId), registration);
            return null;
        });
    }

    // DĂ©sinscription avec quota (transaction): dĂŠcrĂŠmente et supprime l'inscription
    public Task<Void> unregisterWithQuota(String eventId, String registrationId) {
        return db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnap = transaction.get(db.collection(COLLECTION_NAME).document(eventId));
            if (!eventSnap.exists()) {
                throw new IllegalStateException("L'événement n'existe pas");
            }

            Long curP = eventSnap.getLong("currentParticipants");
            int cur = curP != null ? curP.intValue() : 0;
            int newCount = Math.max(0, cur - 1);

            transaction.update(eventSnap.getReference(), "currentParticipants", newCount);
            transaction.delete(db.collection("event_registrations").document(registrationId));
            return null;
        });
    }
}
