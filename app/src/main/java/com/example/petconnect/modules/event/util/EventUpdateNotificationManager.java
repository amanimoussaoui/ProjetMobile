package com.example.petconnect.modules.event.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import com.example.petconnect.R;
import com.example.petconnect.modules.event.activity.EventDetailActivity;
import com.example.petconnect.modules.event.broadcast.UpdateDismissBroadcastReceiver;
import java.util.List;

public class EventUpdateNotificationManager {
    private static final String CHANNEL_ID = "event_updates";
    private static final String CHANNEL_NAME = "Mises à jour d'événements";
    private Context context;

    public EventUpdateNotificationManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription("Notifications de mises à jour sur les événements");
            channel.setShowBadge(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 250, 250, 250});
            
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            android.media.AudioAttributes audioAttributes = new android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .build();
            channel.setSound(soundUri, audioAttributes);
            
            NotificationManager notificationManager = 
                context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void notifyParticipantsOfChanges(String eventId, String eventTitle, 
                                           List<EventChangeDetector.EventChange> changes) {
        if (changes == null || changes.isEmpty()) {
            return;
        }

        // Construire le message de notification
        StringBuilder changeMessage = new StringBuilder();
        for (int i = 0; i < changes.size(); i++) {
            changeMessage.append("• ").append(changes.get(i).type.getLabel());
            if (i < changes.size() - 1) {
                changeMessage.append("\n");
            }
        }

        // Intent pour ouvrir les détails
        Intent detailIntent = new Intent(context, EventDetailActivity.class);
        detailIntent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, eventId);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent detailPendingIntent = PendingIntent.getActivity(
            context,
            eventId.hashCode() + 1000,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Intent pour ignorer
        Intent dismissIntent = new Intent(context, UpdateDismissBroadcastReceiver.class);
        dismissIntent.putExtra("event_id", eventId);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.hashCode() + 2000,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Déterminer l'icône et la couleur selon le type de changement
        EventChangeDetector.EventChange primaryChange = changes.get(0);
        int notificationColor = getColorForChangeType(primaryChange.type);
        boolean isUrgent = primaryChange.type == EventChangeDetector.ChangeType.CANCELLED ||
                          primaryChange.type == EventChangeDetector.ChangeType.DATE_CHANGED;

        String title = "Mise à jour : " + eventTitle;
        String contentText = changeMessage.length() > 50 ? 
            primaryChange.type.getLabel() : changeMessage.toString();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_event)
            .setColor(notificationColor)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(changeMessage.toString()))
            .setContentIntent(detailPendingIntent)
            .setAutoCancel(true)
            .setPriority(isUrgent ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(isUrgent ? NotificationCompat.CATEGORY_ALARM : NotificationCompat.CATEGORY_STATUS)
            .setNumber(changes.size())
            .setShowWhen(true)
            .addAction(R.drawable.ic_event, "Voir détails", detailPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Ignorer", dismissPendingIntent)
            .setVibrate(isUrgent ? new long[]{0, 500, 200, 500} : new long[]{0, 250, 250, 250});

        // Ajouter son pour notifications urgentes
        if (isUrgent) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        NotificationManager notificationManager = 
            context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(eventId.hashCode(), builder.build());
        }

        android.util.Log.d("EventUpdateNotif", "Notification envoyée pour " + eventTitle + 
            " avec " + changes.size() + " changements (Urgent: " + isUrgent + ")");
    }

    private int getColorForChangeType(EventChangeDetector.ChangeType type) {
        switch (type) {
            case CANCELLED:
                return 0xFFE74C3C; // Red
            case DATE_CHANGED:
                return 0xFF3498DB; // Blue
            case LOCATION_CHANGED:
                return 0xFF2ECC71; // Green
            case CAPACITY_CHANGED:
                return 0xFFF39C12; // Orange
            default:
                return 0xFF9B59B6; // Purple
        }
    }
}
