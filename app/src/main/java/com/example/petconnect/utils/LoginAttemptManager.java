package com.example.petconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LoginAttemptManager {
    private static final String PREF_NAME = "login_attempts";
    private static final String KEY_ATTEMPTS = "attempts_";
    private static final String KEY_LOCKED = "locked_";
    private static final String KEY_EMAIL_SENT = "email_sent_";
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_DURATION = 15 * 60 * 1000; // 15 minutes
    private static final String TAG = "LoginAttemptManager";

    private SharedPreferences preferences;
    private Context context;

    public LoginAttemptManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void incrementAttempts(String email) {
        String key = KEY_ATTEMPTS + email;
        int attempts = preferences.getInt(key, 0) + 1;
        preferences.edit().putInt(key, attempts).apply();

        if (attempts >= MAX_ATTEMPTS) {
            lockAccount(email);
            // Envoyer un email d'avertissement si ce n'est pas déjà fait
            sendLockEmailIfNeeded(email);
        }
    }

    public void resetAttempts(String email) {
        String key = KEY_ATTEMPTS + email;
        preferences.edit().remove(key).apply();
        preferences.edit().remove(KEY_LOCKED + email).apply();
        preferences.edit().remove(KEY_EMAIL_SENT + email).apply();
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
        Log.d(TAG, "Account locked for email: " + email);
    }

    /**
     * Envoie un email d'avertissement si le compte est verrouillé et que l'email n'a pas encore été envoyé
     */
    private void sendLockEmailIfNeeded(String email) {
        String emailSentKey = KEY_EMAIL_SENT + email;
        boolean emailAlreadySent = preferences.getBoolean(emailSentKey, false);
        
        if (!emailAlreadySent && email != null && !email.isEmpty()) {
            // Envoyer l'email d'avertissement
            sendLockWarningEmail(email);
            // Marquer que l'email a été envoyé
            preferences.edit().putBoolean(emailSentKey, true).apply();
        }
    }

    /**
     * Envoie un email d'avertissement de verrouillage de compte
     * Cet email est une ALERTE DE SÉCURITÉ, pas un email de réinitialisation de mot de passe
     */
    private void sendLockWarningEmail(String email) {
        EmailService emailService = EmailService.getInstance();
        
        String subject = "⚠️ Alerte de sécurité - Tentatives de connexion échouées - PetConnect";
        String body = "Bonjour,\n\n" +
                "🔒 ALERTE DE SÉCURITÉ\n\n" +
                "Nous vous informons que 3 tentatives de connexion ont échoué sur votre compte PetConnect.\n\n" +
                "Votre compte a été temporairement verrouillé pour 15 minutes pour des raisons de sécurité.\n\n" +
                "❓ ÉTAIT-CE VOUS ?\n\n" +
                "Si OUI, c'était vous qui avez tenté de vous connecter :\n" +
                "✅ Attendez 15 minutes avant de réessayer\n" +
                "✅ Assurez-vous d'utiliser le bon mot de passe\n" +
                "✅ Si vous avez oublié votre mot de passe, utilisez la fonction \"Mot de passe oublié\" dans l'application\n\n" +
                "Si NON, ce n'était PAS vous :\n" +
                "🚨 Changez immédiatement votre mot de passe en utilisant \"Mot de passe oublié\"\n" +
                "🚨 Vérifiez l'activité de votre compte\n" +
                "🚨 Contactez le support si vous suspectez une activité suspecte\n\n" +
                "---\n" +
                "Cet email est une ALERTE DE SÉCURITÉ pour vous informer de l'activité sur votre compte.\n" +
                "Ce n'est PAS un email de réinitialisation de mot de passe.\n" +
                "Si vous souhaitez réinitialiser votre mot de passe, utilisez la fonction \"Mot de passe oublié\" dans l'application.\n\n" +
                "Cordialement,\n" +
                "L'équipe PetConnect\n\n" +
                "---\n" +
                "Cet email a été envoyé automatiquement pour votre sécurité.";

        // Envoyer uniquement un email d'alerte de sécurité (PAS de réinitialisation de mot de passe)
        emailService.queueEmailWithImage(
                email,
                subject,
                body,
                null, // Pas d'image pour cet email
                new EmailService.EmailCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Security alert email queued successfully for: " + email);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error queueing security alert email: " + errorMessage);
                        // Note: Si EmailService échoue, on pourrait utiliser Firebase Auth sendPasswordResetEmail
                        // mais seulement comme dernier recours et avec un message clair que c'est une alerte
                    }
                }
        );
    }

    public boolean shouldRequestPhotoVerification(String email) {
        return getAttempts(email) >= MAX_ATTEMPTS;
    }
}


