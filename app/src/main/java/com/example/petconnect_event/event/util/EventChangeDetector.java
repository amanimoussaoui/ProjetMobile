package com.example.petconnect_event.event.util;

import com.example.petconnect_event.event.model.Event;
import java.util.ArrayList;
import java.util.List;

public class EventChangeDetector {
    public enum ChangeType {
        LOCATION_CHANGED("Lieu changé"),
        DATE_CHANGED("Date reportée"),
        CAPACITY_CHANGED("Places libérées"),
        CANCELLED("Événement annulé"),
        DESCRIPTION_CHANGED("Description modifiée"),
        TITLE_CHANGED("Titre modifié");

        private String label;

        ChangeType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class EventChange {
        public ChangeType type;
        public String oldValue;
        public String newValue;
        public String description;

        public EventChange(ChangeType type, String oldValue, String newValue, String description) {
            this.type = type;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.description = description;
        }
    }

    public static List<EventChange> detectChanges(Event oldEvent, Event newEvent) {
        List<EventChange> changes = new ArrayList<>();

        if (oldEvent == null || newEvent == null) {
            return changes;
        }

        // Vérifier changement de location
        if (!isSame(oldEvent.getLocation(), newEvent.getLocation())) {
            changes.add(new EventChange(
                ChangeType.LOCATION_CHANGED,
                oldEvent.getLocation(),
                newEvent.getLocation(),
                "Le lieu a changé de " + oldEvent.getLocation() + " à " + newEvent.getLocation()
            ));
        }

        // Vérifier changement de date
        if (!isSame(oldEvent.getEventDate(), newEvent.getEventDate())) {
            changes.add(new EventChange(
                ChangeType.DATE_CHANGED,
                formatDate(oldEvent.getEventDate()),
                formatDate(newEvent.getEventDate()),
                "La date a été reportée"
            ));
        }

        // Vérifier changement de capacité
        if (oldEvent.getMaxParticipants() != newEvent.getMaxParticipants()) {
            changes.add(new EventChange(
                ChangeType.CAPACITY_CHANGED,
                String.valueOf(oldEvent.getMaxParticipants()),
                String.valueOf(newEvent.getMaxParticipants()),
                "Les places disponibles ont changé"
            ));
        }

        // Vérifier changement de statut actif (annulation)
        if (oldEvent.isActive() && !newEvent.isActive()) {
            changes.add(new EventChange(
                ChangeType.CANCELLED,
                "Actif",
                "Annulé",
                "L'événement a été annulé"
            ));
        }

        // Vérifier changement de description
        if (!isSame(oldEvent.getDescription(), newEvent.getDescription())) {
            changes.add(new EventChange(
                ChangeType.DESCRIPTION_CHANGED,
                oldEvent.getDescription(),
                newEvent.getDescription(),
                "La description a été modifiée"
            ));
        }

        // Vérifier changement de titre
        if (!isSame(oldEvent.getTitle(), newEvent.getTitle())) {
            changes.add(new EventChange(
                ChangeType.TITLE_CHANGED,
                oldEvent.getTitle(),
                newEvent.getTitle(),
                "Le titre a changé"
            ));
        }

        return changes;
    }

    private static boolean isSame(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) return true;
        if (obj1 == null || obj2 == null) return false;
        return obj1.equals(obj2);
    }

    private static String formatDate(java.util.Date date) {
        if (date == null) return "Non défini";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale.FRENCH);
        return sdf.format(date);
    }
}
