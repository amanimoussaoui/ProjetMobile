package com.example.petconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petconnect.utils.FirebaseAuthManager;
import com.example.petconnect.utils.InputValidator;
import com.example.petconnect.utils.LoginAttemptManager;
import com.example.petconnect.utils.SocialAuthManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.card.MaterialCardView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private FirebaseAuthManager authManager;
    private LoginAttemptManager attemptManager;
    private SocialAuthManager socialAuthManager;
    private CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = FirebaseAuthManager.getInstance();
        attemptManager = new LoginAttemptManager(this);
        socialAuthManager = new SocialAuthManager(this);
        
        // Initialize Facebook callback manager
        facebookCallbackManager = CallbackManager.Factory.create();
        setupFacebookCallback();

        // Check if user is already logged in
        if (authManager.isUserLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialisation des vues
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailEditText = emailLayout.getEditText();
        passwordEditText = passwordLayout.getEditText();
        progressBar = findViewById(R.id.progressBar);

        // Le toggle de mot de passe est géré automatiquement par TextInputLayout avec endIconMode="password_toggle"
        
        // Validation en temps réel pour l'email
        if (emailEditText != null) {
            emailEditText.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String email = s.toString().trim();
                    if (email.length() > 0) {
                        InputValidator.ValidationResult result = InputValidator.validateEmail(email);
                        if (!result.isValid()) {
                            emailLayout.setError(result.getErrorMessage());
                        } else {
                            emailLayout.setError(null);
                        }
                    } else {
                        emailLayout.setError(null);
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
        
        // Validation en temps réel pour le mot de passe avec indicateur de force
        if (passwordEditText != null) {
            passwordEditText.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String password = s.toString();
                    if (password.length() > 0) {
                        InputValidator.PasswordStrength strength = InputValidator.getPasswordStrength(password);
                        String hint = "";
                        switch (strength) {
                            case WEAK:
                                hint = "Mot de passe faible";
                                passwordLayout.setError(hint);
                                break;
                            case MEDIUM:
                                hint = "Mot de passe moyen";
                                passwordLayout.setError(null);
                                passwordLayout.setHelperText(hint);
                                break;
                            case STRONG:
                            case VERY_STRONG:
                                passwordLayout.setError(null);
                                passwordLayout.setHelperText("Mot de passe fort ✓");
                                break;
                            default:
                                passwordLayout.setError(null);
                                passwordLayout.setHelperText(null);
                        }
                    } else {
                        passwordLayout.setError(null);
                        passwordLayout.setHelperText(null);
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }

        // Lien "Forgot Password"
        TextView forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
        if (forgotPasswordLink != null) {
            forgotPasswordLink.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }

        // Create Account Link
        TextView createAccountLink = findViewById(R.id.createAccountLink);
        if (createAccountLink != null) {
            createAccountLink.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        // Login Button
        Button loginButton = findViewById(R.id.loginButton);
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> handleLogin());
        }

        // Google Sign In Button (ImageView now)
        ImageView googleSignInButton = findViewById(R.id.googleSignInButton);
        if (googleSignInButton != null) {
            googleSignInButton.setOnClickListener(v -> handleGoogleSignIn());
        }
        
        // Google CardView clickable
        MaterialCardView googleButtonCard = findViewById(R.id.googleButtonCard);
        if (googleButtonCard != null) {
            googleButtonCard.setOnClickListener(v -> handleGoogleSignIn());
        }

        // Facebook Sign In Button (ImageView now)
        ImageView facebookSignInButton = findViewById(R.id.facebookSignInButton);
        if (facebookSignInButton != null) {
            facebookSignInButton.setOnClickListener(v -> handleFacebookSignIn());
        }
        
        // Facebook CardView clickable
        MaterialCardView facebookButtonCard = findViewById(R.id.facebookButtonCard);
        if (facebookButtonCard != null) {
            facebookButtonCard.setOnClickListener(v -> handleFacebookSignIn());
        }
        
        // Setup animations
        setupAnimations();
    }

    private void setupFacebookCallback() {
        com.facebook.login.LoginManager.getInstance().registerCallback(
            facebookCallbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    socialAuthManager.handleFacebookSignInResult(
                        loginResult,
                        new SocialAuthManager.SocialAuthCallback() {
                            @Override
                            public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                                navigateToHome();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    );
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, "Facebook login cancelled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(LoginActivity.this, "Facebook login error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void handleGoogleSignIn() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        socialAuthManager.signInWithGoogle();
    }

    private void handleFacebookSignIn() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        socialAuthManager.signInWithFacebook();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Handle Facebook callback
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        
        // Handle Google Sign In
        if (requestCode == SocialAuthManager.RC_GOOGLE_SIGN_IN) {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            socialAuthManager.handleGoogleSignInResult(
                data,
                new SocialAuthManager.SocialAuthCallback() {
                    @Override
                    public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                        navigateToHome();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            );
        }
    }

    private void navigateToHome() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void setupAnimations() {
        // Logo animation with zoom out
        ImageView logoImageView = findViewById(R.id.logoImageView);
        if (logoImageView != null) {
            Animation logoZoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
            logoZoomOut.setInterpolator(new OvershootInterpolator());
            logoZoomOut.setDuration(1200);
            logoImageView.startAnimation(logoZoomOut);
        }

        // Animation du titre
        TextView welcomeTitleTextView = findViewById(R.id.welcomeTitleTextView);
        if (welcomeTitleTextView != null) {
            Animation titleFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            titleFadeIn.setStartOffset(400);
            titleFadeIn.setDuration(800);
            welcomeTitleTextView.startAnimation(titleFadeIn);
        }

        // Animation du sous-titre
        TextView subtitleTextView = findViewById(R.id.subtitleTextView);
        if (subtitleTextView != null) {
            Animation subtitleFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            subtitleFadeIn.setStartOffset(600);
            subtitleFadeIn.setDuration(800);
            subtitleTextView.startAnimation(subtitleFadeIn);
        }

        // Animation pour les champs avec slide up
        if (emailLayout != null) {
            Animation fieldAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
            fieldAnimation.setStartOffset(800);
            fieldAnimation.setDuration(1000);
            fieldAnimation.setInterpolator(new DecelerateInterpolator());
            emailLayout.startAnimation(fieldAnimation);
        }

        if (passwordLayout != null) {
            Animation fieldAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
            fieldAnimation.setStartOffset(1000);
            fieldAnimation.setDuration(1000);
            fieldAnimation.setInterpolator(new DecelerateInterpolator());
            passwordLayout.startAnimation(fieldAnimation);
        }

        // Animation pour le lien "Forgot Password"
        TextView forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
        if (forgotPasswordLink != null) {
            Animation linkFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            linkFadeIn.setStartOffset(1200);
            linkFadeIn.setDuration(800);
            forgotPasswordLink.startAnimation(linkFadeIn);
        }

        // Animation pour le bouton login avec slide up et pulse
        MaterialCardView loginButtonCard = findViewById(R.id.loginButtonCard);
        if (loginButtonCard != null) {
            Animation buttonSlideUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
            buttonSlideUp.setStartOffset(1400);
            buttonSlideUp.setDuration(1000);
            buttonSlideUp.setInterpolator(new OvershootInterpolator());
            loginButtonCard.startAnimation(buttonSlideUp);

            // Animation pulse continue pour le bouton
            Animation buttonPulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
            buttonPulse.setStartOffset(2400);
            buttonPulse.setDuration(1500);
            buttonPulse.setRepeatCount(Animation.INFINITE);
            buttonPulse.setRepeatMode(Animation.REVERSE);
            loginButtonCard.startAnimation(buttonPulse);
        }

        // Animation for divider
        TextView dividerTextView = findViewById(R.id.dividerTextView);
        if (dividerTextView != null) {
            Animation dividerFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            dividerFadeIn.setStartOffset(1600);
            dividerFadeIn.setDuration(800);
            dividerTextView.startAnimation(dividerFadeIn);
        }

        // Animation for social login icons
        LinearLayout socialLoginLayout = findViewById(R.id.socialLoginLayout);
        if (socialLoginLayout != null) {
            Animation socialFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            socialFadeIn.setStartOffset(1800);
            socialFadeIn.setDuration(1000);
            socialLoginLayout.startAnimation(socialFadeIn);
        }

        // Animation for Create Account Link
        TextView createAccountLink = findViewById(R.id.createAccountLink);
        if (createAccountLink != null) {
            Animation linkFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            linkFadeIn.setStartOffset(2200);
            linkFadeIn.setDuration(800);
            createAccountLink.startAnimation(linkFadeIn);
        }
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Réinitialiser les erreurs
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Validation de l'email avec InputValidator
        InputValidator.ValidationResult emailValidation = InputValidator.validateEmail(email);
        if (!emailValidation.isValid()) {
            emailLayout.setError(emailValidation.getErrorMessage());
            return;
        }

        // Validation du mot de passe avec InputValidator
        InputValidator.ValidationResult passwordValidation = InputValidator.validatePassword(password);
        if (!passwordValidation.isValid()) {
            passwordLayout.setError(passwordValidation.getErrorMessage());
            return;
        }

        // Check if account is locked
        if (attemptManager.isLocked(email)) {
            long remainingTime = attemptManager.getRemainingLockTime(email);
            int minutes = (int) (remainingTime / (60 * 1000));
            String lockMessage = "⚠️ Compte verrouillé après 3 tentatives échouées.\n" +
                    "Un email d'avertissement a été envoyé à votre adresse.\n" +
                    "Réessayez dans " + minutes + " minute(s).";
            Toast.makeText(this, lockMessage, Toast.LENGTH_LONG).show();
            return;
        }

        // Check if photo verification is required
        if (attemptManager.shouldRequestPhotoVerification(email)) {
            Intent intent = new Intent(LoginActivity.this, PhotoVerificationActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            return;
        }

        // Show progress bar
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.loginButton).setEnabled(false);

        // Login with Firebase Auth
        authManager.login(email, password, this, new FirebaseAuthManager.AuthCallback() {
            @Override
            public void onSuccess(com.google.firebase.auth.FirebaseUser user) {
                // Reset attempts on success
                attemptManager.resetAttempts(email);
                
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                findViewById(R.id.loginButton).setEnabled(true);
                
                navigateToHome();
            }

            @Override
            public void onError(String errorMessage) {
                // Shake animation on error
                MaterialCardView loginButtonCard = findViewById(R.id.loginButtonCard);
                if (loginButtonCard != null) {
                    loginButtonCard.animate()
                            .translationX(-20)
                            .setDuration(50)
                            .withEndAction(() -> {
                                loginButtonCard.animate()
                                        .translationX(20)
                                        .setDuration(50)
                                        .withEndAction(() -> {
                                            loginButtonCard.animate()
                                                    .translationX(-20)
                                                    .setDuration(50)
                                                    .withEndAction(() -> {
                                                        loginButtonCard.animate()
                                                                .translationX(0)
                                                                .setDuration(50)
                                                                .start();
                                                    })
                                                    .start();
                                        })
                                        .start();
                            })
                            .start();
                }
                
                // Increment attempt counter on error
                attemptManager.incrementAttempts(email);
                int attempts = attemptManager.getAttempts(email);
                
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                findViewById(R.id.loginButton).setEnabled(true);
                
                // Display error with remaining attempts
                String errorMsg = errorMessage;
                if (attempts >= 3) {
                    errorMsg += "\n⚠️ 3 tentatives échouées. Compte verrouillé.\n" +
                            "Un email d'avertissement a été envoyé à votre adresse.\n" +
                            "Votre compte est verrouillé pour 15 minutes.";
                    // Afficher le message et ne pas rediriger vers photo verification
                    // car le compte est maintenant verrouillé
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    errorMsg += "\n⚠️ Tentatives restantes: " + (3 - attempts);
                }
                
                if (errorMessage.contains("email")) {
                    emailLayout.setError(errorMsg);
                } else if (errorMessage.contains("password") || errorMessage.contains("mot de passe")) {
                    passwordLayout.setError(errorMsg);
                } else {
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}

