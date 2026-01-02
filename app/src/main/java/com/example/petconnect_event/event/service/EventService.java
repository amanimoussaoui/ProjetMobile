package com.example.petconnect_event.event.service;

import com.example.petconnect_event.event.model.Event;
import com.example.petconnect_event.event.model.EventRegistration;
import com.example.petconnect_event.event.repository.EventRepository;
import com.example.petconnect_event.event.repository.EventRegistrationRepository;

public class EventService {
    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;

    public EventService() {
        this.eventRepository = new EventRepository();
        this.registrationRepository = new EventRegistrationRepository();
    }

    // MĂ©thodes de logique mĂ©tier Ă  implĂ©menter
    public void registerForEvent(Event event, String userId, String userName, String userEmail) {
        // TODO: Implémenter la logique d'inscription
    }

    public void cancelRegistration(String registrationId) {
        // TODO: Implémenter la logique d'annulation
    }

    public boolean canRegister(Event event) {
        // TODO: VĂ©rifier si l'utilisateur peut s'inscrire
        return event.getCurrentParticipants() < event.getMaxParticipants();
    }
}