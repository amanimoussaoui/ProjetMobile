package com.example.petconnect.modules.shop.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class PaymentService {
    private static final String TAG = "PaymentService";
    private PaymentCallback currentCallback;

    public interface PaymentCallback {
        void onPaymentSuccess(String paymentId);
        void onPaymentCancelled();
        void onPaymentError(String error);
        void onError(String error);
    }

    public void processPayment(Activity activity, double amount, String description, PaymentCallback callback) {
        this.currentCallback = callback;

        if (amount <= 0) {
            if (callback != null) {
                callback.onError("Montant invalide");
            }
            return;
        }

        // Afficher un dialogue de paiement simple
        showPaymentDialog(activity, amount, description);
    }

    private void showPaymentDialog(Activity activity, double amount, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Paiement Stripe");
        builder.setMessage(String.format("Montant à payer: %.2f €\n\n%s", amount, description));

        // Créer un EditText pour les informations de carte (simulation)
        final EditText cardNumberInput = new EditText(activity);
        cardNumberInput.setHint("Numéro de carte (ex: 4242 4242 4242 4242)");
        cardNumberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        cardNumberInput.setPadding(50, 20, 50, 20);

        final EditText expiryInput = new EditText(activity);
        expiryInput.setHint("MM/AA (ex: 12/25)");
        expiryInput.setInputType(InputType.TYPE_CLASS_TEXT);
        expiryInput.setPadding(50, 20, 50, 20);

        final EditText cvvInput = new EditText(activity);
        cvvInput.setHint("CVV (ex: 123)");
        cvvInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        cvvInput.setPadding(50, 20, 50, 20);

        // Créer un layout pour les champs
        android.widget.LinearLayout layout = new android.widget.LinearLayout(activity);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(cardNumberInput);
        layout.addView(expiryInput);
        layout.addView(cvvInput);

        builder.setView(layout);

        builder.setPositiveButton("Payer", (dialog, which) -> {
            String cardNumber = cardNumberInput.getText().toString().trim();
            String expiry = expiryInput.getText().toString().trim();
            String cvv = cvvInput.getText().toString().trim();

            // Validation simple
            if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(activity, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                showPaymentDialog(activity, amount, description); // Réafficher le dialogue
                return;
            }

            // Simuler le traitement du paiement
            processStripePayment(activity, cardNumber, expiry, cvv, amount);
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> {
            if (currentCallback != null) {
                currentCallback.onPaymentCancelled();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void processStripePayment(Activity activity, String cardNumber, String expiry, String cvv, double amount) {
        // Simuler le traitement du paiement avec Stripe
        // Dans une vraie application, vous enverriez ces données à votre backend
        // qui créerait un PaymentIntent et traiterait le paiement

        Toast.makeText(activity, "Traitement du paiement en cours...", Toast.LENGTH_SHORT).show();

        // Simuler un délai de traitement
        new android.os.Handler().postDelayed(() -> {
            // Simuler un paiement réussi
            String paymentId = "pay_" + System.currentTimeMillis();
            Log.d(TAG, "Paiement simulé réussi: " + paymentId);

            // Sauvegarder les informations de paiement dans Firebase
            savePaymentToFirebase(paymentId, amount, cardNumber);

            if (currentCallback != null) {
                currentCallback.onPaymentSuccess(paymentId);
            }
        }, 2000);
    }

    private void savePaymentToFirebase(String paymentId, double amount, String cardNumber) {
        // Sauvegarder les informations de paiement dans Firebase
        // Note: Dans une vraie application, ne JAMAIS sauvegarder les numéros de carte complets
        // Seul le backend Stripe devrait gérer ces informations
        try {
            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
            com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            String userId = currentUser != null ? currentUser.getUid() : "local_user";

            java.util.Map<String, Object> paymentData = new java.util.HashMap<>();
            paymentData.put("paymentId", paymentId);
            paymentData.put("amount", amount);
            paymentData.put("cardLast4", cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "****");
            paymentData.put("timestamp", com.google.firebase.Timestamp.now());
            paymentData.put("status", "completed");

            db.collection("PetConnect")
                    .document("payments")
                    .collection(userId)
                    .document(paymentId)
                    .set(paymentData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Paiement sauvegardé dans Firebase"))
                    .addOnFailureListener(e -> Log.e(TAG, "Erreur sauvegarde paiement: " + e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "Erreur sauvegarde paiement Firebase: " + e.getMessage());
        }
    }
}
