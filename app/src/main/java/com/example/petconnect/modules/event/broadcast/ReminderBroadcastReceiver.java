package com.example.petconnect.modules.event.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.petconnect.modules.event.util.EventNotificationManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String eventId = intent.getStringExtra("event_id");
            String eventTitle = intent.getStringExtra("event_title");
            long eventTime = intent.getLongExtra("event_time", 0);

            // Formater l'heure de l'événement
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.FRENCH);
            String formattedTime = sdf.format(new Date(eventTime));

            // Afficher la notification
            EventNotificationManager notificationManager = new EventNotificationManager(context);
            notificationManager.showEventReminder(eventId, eventTitle, "à " + formattedTime);

            android.util.Log.d("ReminderReceiver", "Notification envoyée pour: " + eventTitle);
        } catch (Exception e) {
            android.util.Log.e("ReminderReceiver", "Erreur lors de la notification", e);
        }
    }
}
