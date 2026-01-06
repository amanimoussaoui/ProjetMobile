package com.example.petconnect.modules.event.service;

import com.example.petconnect.modules.event.model.Event;
import com.example.petconnect.modules.event.model.EventRegistration;
import com.example.petconnect.modules.event.repository.EventRepository;
import com.example.petconnect.modules.event.repository.EventRegistrationRepository;

public class EventService {
    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;

    public EventService() {
        this.eventRepository = new EventRepository();
        this.registrationRepository = new EventRegistrationRepository();
    }

    // Méthodes de logique métier à implémenter
    public void registerForEvent(Event event, String userId, String userName, String userEmail) {
        // TODO: Implémenter la logique d'inscription
    }

    public void cancelRegistration(String registrationId) {
        // TODO: Implémenter la logique d'annulation
    }

    public boolean canRegister(Event event) {
        // TODO: Vérifier si l'utilisateur peut s'inscrire
        return event.getCurrentParticipants() < event.getMaxParticipants();
    }
}
