package com.example.petconnect.utils;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmailService {
    private static final String TAG = "EmailService";
    private static final String EMAIL_QUEUE_COLLECTION = "email_queue";
    private static EmailService instance;
    private FirebaseFirestore firestore;

    private EmailService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    /**
     * Ajoute une demande d'envoi d'email à la queue Firestore
     * Une Firebase Function ou un backend peut traiter cette queue
     * 
     * @param to Email du destinataire
     * @param subject Sujet de l'email
     * @param body Corps de l'email
     * @param imageUrl URL de l'image à joindre
     * @param callback Callback pour le résultat
     */
    public void queueEmailWithImage(String to, String subject, String body, String imageUrl, EmailCallback callback) {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("to", to);
        emailData.put("subject", subject);
        emailData.put("body", body);
        emailData.put("imageUrl", imageUrl);
        emailData.put("status", "pending");
        emailData.put("createdAt", System.currentTimeMillis());
        emailData.put("type", "verification_photo");

        firestore.collection(EMAIL_QUEUE_COLLECTION)
                .add(emailData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Email queued successfully: " + documentReference.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error queueing email", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Envoie un email de vérification avec photo
     * 
     * @param userEmail Email de l'utilisateur
     * @param imageUrl URL de l'image dans Firebase Storage
     * @param callback Callback pour le résultat
     */
    public void sendVerificationPhotoEmail(String userEmail, String imageUrl, EmailCallback callback) {
        String subject = "Vérification de sécurité - PetConnect";
        String body = "Bonjour,\n\n" +
                "Une tentative de connexion avec votre compte a échoué 3 fois.\n\n" +
                "Pour des raisons de sécurité, veuillez vérifier que c'était bien vous en examinant la photo jointe.\n\n" +
                "Si ce n'était pas vous, veuillez changer votre mot de passe immédiatement.\n\n" +
                "Cordialement,\nL'équipe PetConnect";

        queueEmailWithImage(userEmail, subject, body, imageUrl, callback);
    }

    public interface EmailCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}


