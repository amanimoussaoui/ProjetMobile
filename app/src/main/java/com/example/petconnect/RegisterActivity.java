package com.example.petconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petconnect.models.User;
import com.example.petconnect.utils.FirebaseAuthManager;
import com.example.petconnect.utils.UserManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout firstNameLayout;
    private TextInputLayout lastNameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private ProgressBar progressBar;
    private FirebaseAuthManager authManager;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = FirebaseAuthManager.getInstance();
        userManager = UserManager.getInstance();

        // Animation slide-in
        View rootView = findViewById(android.R.id.content);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        rootView.startAnimation(slideIn);

        // Initialisation des vues
        firstNameLayout = findViewById(R.id.firstNameLayout);
        lastNameLayout = findViewById(R.id.lastNameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        progressBar = findViewById(R.id.progressBar);

        // Le toggle de mot de passe est géré automatiquement par TextInputLayout avec endIconMode="password_toggle"

        // Lien "Already have an account? Login"
        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Bouton Create Account
        findViewById(R.id.createAccountButton).setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String firstName = firstNameLayout.getEditText().getText().toString().trim();
        String lastName = lastNameLayout.getEditText().getText().toString().trim();
        String email = emailLayout.getEditText().getText().toString().trim();
        String password = passwordLayout.getEditText().getText().toString().trim();

        // Réinitialiser les erreurs
        firstNameLayout.setError(null);
        lastNameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Validation
        if (firstName.isEmpty()) {
            firstNameLayout.setError("Veuillez entrer votre prénom");
            return;
        }
        if (lastName.isEmpty()) {
            lastNameLayout.setError("Veuillez entrer votre nom");
            return;
        }
        if (email.isEmpty()) {
            emailLayout.setError("Veuillez entrer votre email");
            return;
        }
        if (password.isEmpty()) {
            passwordLayout.setError("Veuillez entrer votre mot de passe");
            return;
        }
        if (password.length() < 6) {
            passwordLayout.setError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        // Afficher le progress bar
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.createAccountButton).setEnabled(false);

        // Créer le compte avec Firebase Auth
        authManager.register(email, password, this, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                // Créer l'objet User
                User user = new User(
                        firebaseUser.getUid(),
                        firstName,
                        lastName,
                        email
                );
                user.setRole("user"); // Par défaut

                // Enregistrer l'utilisateur dans Firestore
                userManager.createUser(user, new UserManager.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        findViewById(R.id.createAccountButton).setEnabled(true);
                        
                        Toast.makeText(RegisterActivity.this, 
                                "Compte créé avec succès ! Veuillez vérifier votre email.", 
                                Toast.LENGTH_LONG).show();
                        
                        // Retourner à LoginActivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        findViewById(R.id.createAccountButton).setEnabled(true);
                        
                        Toast.makeText(RegisterActivity.this, 
                                "Erreur lors de la création du profil: " + errorMessage, 
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                findViewById(R.id.createAccountButton).setEnabled(true);
                
                // Afficher l'erreur
                if (errorMessage.contains("email")) {
                    emailLayout.setError(errorMessage);
                } else if (errorMessage.contains("password") || errorMessage.contains("mot de passe")) {
                    passwordLayout.setError(errorMessage);
                } else {
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

