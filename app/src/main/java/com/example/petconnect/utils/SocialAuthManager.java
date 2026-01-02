package com.example.petconnect.utils;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

/**
 * Gestionnaire pour les authentifications sociales (Google, Facebook, GitHub)
 */
public class SocialAuthManager {
    public static final int RC_GOOGLE_SIGN_IN = 9001;
    public static final int RC_FACEBOOK_SIGN_IN = 64206;
    public static final int RC_GITHUB_SIGN_IN = 64207;
    
    private AppCompatActivity activity;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    
    public SocialAuthManager(AppCompatActivity activity) {
        this.activity = activity;
        this.firebaseAuth = FirebaseAuth.getInstance();
        // Initialiser Google Sign-In de manière sécurisée
        try {
            setupGoogleSignIn();
        } catch (Exception e) {
            android.util.Log.e("SocialAuthManager", "Erreur lors de l'initialisation Google Sign-In: " + e.getMessage(), e);
            // Continuer sans Google Sign-In pour éviter le crash
        }
    }
    
    private void setupGoogleSignIn() {
        // Récupérer le Web Client ID depuis google-services.json via les ressources générées
        String webClientId = null;
        
        try {
            // Essayer de récupérer depuis strings.xml
            int resId = activity.getResources().getIdentifier("default_web_client_id", "string", activity.getPackageName());
            if (resId != 0) {
                webClientId = activity.getString(resId);
                // Vérifier si c'est la valeur par défaut
                if (webClientId != null && (webClientId.equals("YOUR_WEB_CLIENT_ID_HERE") || webClientId.isEmpty())) {
                    webClientId = null;
                }
            }
        } catch (Exception e) {
            android.util.Log.d("SocialAuthManager", "default_web_client_id non trouvé dans strings.xml");
        }
        
        // Si pas trouvé, essayer depuis google-services.json (généré automatiquement)
        if (webClientId == null || webClientId.isEmpty()) {
            try {
                int googleClientIdRes = activity.getResources().getIdentifier("google_client_id", "string", activity.getPackageName());
                if (googleClientIdRes != 0) {
                    webClientId = activity.getString(googleClientIdRes);
                }
            } catch (Exception e) {
                android.util.Log.d("SocialAuthManager", "google_client_id non trouvé");
            }
        }
        
        // Si toujours null, ne pas initialiser Google Sign-In (éviter le crash)
        if (webClientId == null || webClientId.isEmpty()) {
            android.util.Log.w("SocialAuthManager", 
                "Web Client ID manquant! Google Sign-In ne sera pas disponible. Ajoutez votre Web Client ID dans strings.xml");
            // Ne pas créer le client Google Sign-In pour éviter le crash
            return;
        }
        
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(activity, gso);
            android.util.Log.d("SocialAuthManager", "Google Sign-In initialisé avec succès");
        } catch (Exception e) {
            android.util.Log.e("SocialAuthManager", "Erreur lors de la création de GoogleSignInClient: " + e.getMessage(), e);
            // Ne pas crasher, juste logger l'erreur
        }
    }
    
    /**
     * Lance la connexion Google
     */
    public void signInWithGoogle() {
        if (googleSignInClient == null) {
            Toast.makeText(activity, 
                "Google Sign-In non configuré. Ajoutez le Web Client ID dans strings.xml", 
                Toast.LENGTH_LONG).show();
            android.util.Log.w("SocialAuthManager", "GoogleSignInClient n'est pas initialisé");
            return;
        }
        try {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        } catch (Exception e) {
            Toast.makeText(activity, "Erreur Google Sign-In: " + e.getMessage(), Toast.LENGTH_LONG).show();
            android.util.Log.e("SocialAuthManager", "Erreur signInWithGoogle", e);
        }
    }
    
    /**
     * Traite le résultat de la connexion Google
     */
    public void handleGoogleSignInResult(Intent data, SocialAuthCallback callback) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(activity, task1 -> {
                            if (task1.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null && callback != null) {
                                    callback.onSuccess(user);
                                }
                            } else {
                                if (callback != null) {
                                    callback.onError(task1.getException() != null ? 
                                        task1.getException().getMessage() : "Erreur de connexion Google");
                                }
                            }
                        });
            }
        } catch (ApiException e) {
            if (callback != null) {
                callback.onError("Erreur de connexion Google: " + e.getMessage());
            }
        }
    }
    
    /**
     * Lance la connexion Facebook
     */
    public void signInWithFacebook() {
        try {
            // Facebook SDK
            com.facebook.login.LoginManager.getInstance().logInWithReadPermissions(
                activity, 
                java.util.Arrays.asList("email", "public_profile")
            );
            
            // Le callback sera géré dans LoginActivity via FacebookCallback
        } catch (Exception e) {
            Toast.makeText(activity, "Erreur Facebook: " + e.getMessage(), Toast.LENGTH_LONG).show();
            android.util.Log.e("SocialAuthManager", "Erreur Facebook", e);
        }
    }
    
    /**
     * Traite le résultat de la connexion Facebook
     */
    public void handleFacebookSignInResult(com.facebook.login.LoginResult loginResult, SocialAuthCallback callback) {
        com.facebook.AccessToken token = loginResult.getAccessToken();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null && callback != null) {
                            callback.onSuccess(user);
                        }
                    } else {
                        if (callback != null) {
                            callback.onError(task.getException() != null ? 
                                task.getException().getMessage() : "Erreur de connexion Facebook");
                        }
                    }
                });
    }
    
    /**
     * Lance la connexion GitHub
     */
    public void signInWithGitHub() {
        try {
            OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
            
            // Ajouter des scopes si nécessaire
            provider.addCustomParameter("prompt", "consent");
            
            firebaseAuth.startActivityForSignInWithProvider(activity, provider.build())
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = authResult.getUser();
                        if (user != null) {
                            Toast.makeText(activity, "Connexion GitHub réussie !", Toast.LENGTH_SHORT).show();
                            // Navigation sera gérée par le callback
                        }
                    })
                    .addOnFailureListener(e -> {
                        String errorMsg = "Erreur de connexion GitHub: " + e.getMessage();
                        Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                        android.util.Log.e("SocialAuthManager", "Erreur GitHub", e);
                    });
        } catch (Exception e) {
            Toast.makeText(activity, "Erreur GitHub: " + e.getMessage(), Toast.LENGTH_LONG).show();
            android.util.Log.e("SocialAuthManager", "Erreur GitHub", e);
        }
    }
    
    public interface SocialAuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String errorMessage);
    }
}

