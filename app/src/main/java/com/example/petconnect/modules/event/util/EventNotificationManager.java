package com.example.petconnect.modules.event.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.petconnect.R;
import com.example.petconnect.modules.event.activity.EventDetailActivity;

public class EventNotificationManager {
    private static final String CHANNEL_ID = "event_reminders";
    private static final String CHANNEL_NAME = "Rappels d'événements";
    private Context context;

    public EventNotificationManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription("Rappels pour les événements PetConnect");
            
            android.app.NotificationManager notificationManager = 
                context.getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showEventReminder(String eventId, String eventTitle, String eventTime) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            eventId.hashCode(), 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_event)
            .setContentTitle("Rappel : " + eventTitle)
            .setContentText("L'événement commence " + eventTime)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER);

        android.app.NotificationManager notificationManager = 
            context.getSystemService(android.app.NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(eventId.hashCode(), builder.build());
        }
    }
}
