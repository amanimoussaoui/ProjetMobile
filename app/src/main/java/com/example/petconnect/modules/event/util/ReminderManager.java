package com.example.petconnect.modules.event.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.petconnect.modules.event.broadcast.ReminderBroadcastReceiver;
import java.util.Calendar;

public class ReminderManager {
    private static final String PREFS_NAME = "petconnect_reminders";
    private static final String KEY_REMINDER_PREFIX = "reminder_";
    private Context context;
    private AlarmManager alarmManager;
    private SharedPreferences preferences;

    public ReminderManager(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setReminder(String eventId, String eventTitle, long eventTimeInMillis, int minutesBefore) {
        if (alarmManager == null) return;

        long reminderTime = eventTimeInMillis - (minutesBefore * 60 * 1000L);
        
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("event_id", eventId);
        intent.putExtra("event_title", eventTitle);
        intent.putExtra("event_time", eventTimeInMillis);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            );
            
            // Sauvegarder la configuration du rappel
            preferences.edit()
                .putLong(KEY_REMINDER_PREFIX + eventId, reminderTime)
                .putInt(KEY_REMINDER_PREFIX + eventId + "_minutes", minutesBefore)
                .apply();

            android.util.Log.d("ReminderManager", "Rappel défini pour " + eventTitle + " dans " + minutesBefore + " minutes");
        } catch (Exception e) {
            android.util.Log.e("ReminderManager", "Erreur lors du paramétrage du rappel", e);
        }
    }

    public void cancelReminder(String eventId) {
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
        
        // Supprimer de la configuration sauvegardée
        preferences.edit()
            .remove(KEY_REMINDER_PREFIX + eventId)
            .remove(KEY_REMINDER_PREFIX + eventId + "_minutes")
            .apply();

        android.util.Log.d("ReminderManager", "Rappel annulé pour l'événement: " + eventId);
    }

    public boolean hasReminder(String eventId) {
        return preferences.contains(KEY_REMINDER_PREFIX + eventId);
    }

    public int getReminderMinutes(String eventId) {
        return preferences.getInt(KEY_REMINDER_PREFIX + eventId + "_minutes", 15);
    }
}
