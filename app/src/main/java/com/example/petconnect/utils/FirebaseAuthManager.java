package com.example.petconnect.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthManager {
    private static FirebaseAuthManager instance;
    private FirebaseAuth firebaseAuth;

    private FirebaseAuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseAuthManager getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthManager();
        }
        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public void login(String email, String password, Context context, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Rafraîchir les données de l'utilisateur pour obtenir le statut de vérification à jour
                            user.reload().addOnCompleteListener(reloadTask -> {
                                FirebaseUser refreshedUser = firebaseAuth.getCurrentUser();
                                if (refreshedUser != null) {
                                    // Pour le développement, permettre la connexion même si l'email n'est pas vérifié
                                    // En production, vous pouvez réactiver la vérification en décommentant les lignes ci-dessous
                                    callback.onSuccess(refreshedUser);
                                    
                                    /* 
                                    // Vérification de l'email (décommentez pour l'activer en production)
                                    if (refreshedUser.isEmailVerified()) {
                                        callback.onSuccess(refreshedUser);
                                    } else {
                                        callback.onError("Veuillez vérifier votre email avant de vous connecter");
                                    }
                                    */
                                } else {
                                    callback.onError("Une erreur est survenue lors du rechargement des données");
                                }
                            });
                        } else {
                            callback.onError("Une erreur est survenue");
                        }
                    } else {
                        String errorMessage = "Échec de la connexion";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        callback.onError(errorMessage);
                    }
                });
    }

    public void register(String email, String password, Context context, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Envoyer l'email de vérification
                            user.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            callback.onSuccess(user);
                                        } else {
                                            callback.onError("Compte créé mais l'email de vérification n'a pas pu être envoyé");
                                        }
                                    });
                        } else {
                            callback.onError("Une erreur est survenue");
                        }
                    } else {
                        String errorMessage = "Échec de l'inscription";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        callback.onError(errorMessage);
                    }
                });
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public void resetPassword(String email, Context context, PasswordResetCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String errorMessage = "Échec de l'envoi de l'email";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        callback.onError(errorMessage);
                    }
                });
    }

    public void changePassword(String newPassword, Context context, PasswordChangeCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String errorMessage = "Échec du changement de mot de passe";
                            if (task.getException() != null) {
                                errorMessage = task.getException().getMessage();
                            }
                            callback.onError(errorMessage);
                        }
                    });
        } else {
            callback.onError("Aucun utilisateur connecté");
        }
    }

    // Interfaces de callback
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String errorMessage);
    }

    public interface PasswordResetCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public interface PasswordChangeCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}



