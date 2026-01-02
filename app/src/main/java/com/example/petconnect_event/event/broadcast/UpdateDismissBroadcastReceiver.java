package com.example.petconnect_event.event.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;

public class UpdateDismissBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String eventId = intent.getStringExtra("event_id");
            
            // Annuler la notification
            NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(eventId.hashCode());
            }

            android.util.Log.d("UpdateDismiss", "Notification annulée pour: " + eventId);
        } catch (Exception e) {
            android.util.Log.e("UpdateDismiss", "Erreur lors de l'annulation", e);
        }
    }
}
