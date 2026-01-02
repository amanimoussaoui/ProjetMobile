package com.example.petconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginAttemptManager {
    private static final String PREF_NAME = "login_attempts";
    private static final String KEY_ATTEMPTS = "attempts_";
    private static final String KEY_LOCKED = "locked_";
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_DURATION = 15 * 60 * 1000; // 15 minutes

    private SharedPreferences preferences;

    public LoginAttemptManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void incrementAttempts(String email) {
        String key = KEY_ATTEMPTS + email;
        int attempts = preferences.getInt(key, 0) + 1;
        preferences.edit().putInt(key, attempts).apply();

        if (attempts >= MAX_ATTEMPTS) {
            lockAccount(email);
        }
    }

    public void resetAttempts(String email) {
        String key = KEY_ATTEMPTS + email;
        preferences.edit().remove(key).apply();
        preferences.edit().remove(KEY_LOCKED + email).apply();
    }

    public int getAttempts(String email) {
        String key = KEY_ATTEMPTS + email;
        return preferences.getInt(key, 0);
    }

    public boolean isLocked(String email) {
        String key = KEY_LOCKED + email;
        long lockTime = preferences.getLong(key, 0);
        if (lockTime == 0) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lockTime > LOCK_DURATION) {
            // Le verrouillage a expiré
            resetAttempts(email);
            return false;
        }
        return true;
    }

    public long getRemainingLockTime(String email) {
        String key = KEY_LOCKED + email;
        long lockTime = preferences.getLong(key, 0);
        if (lockTime == 0) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lockTime;
        long remaining = LOCK_DURATION - elapsed;
        return remaining > 0 ? remaining : 0;
    }

    private void lockAccount(String email) {
        String key = KEY_LOCKED + email;
        preferences.edit().putLong(key, System.currentTimeMillis()).apply();
    }

    public boolean shouldRequestPhotoVerification(String email) {
        return getAttempts(email) >= MAX_ATTEMPTS;
    }
}


