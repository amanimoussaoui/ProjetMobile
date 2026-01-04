package com.Projet.forum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    
    public static String formatTimestamp(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        
        if (seconds < 60) {
            return "À l'instant";
        } else if (minutes < 60) {
            return minutes + " " + "m";
        } else if (hours < 24) {
            return hours + " " + "h";
        } else if (days < 7) {
            return days + " " + "j";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy", Locale.FRENCH);
            return sdf.format(new Date(timestamp));
        }
    }
}
