package com.example.petconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView logoImageView = findViewById(R.id.logoImageView);
        TextView appNameTextView = findViewById(R.id.appNameTextView);
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        TextView subtitleTextView = findViewById(R.id.subtitleTextView);
        LinearLayout iconsLayout = findViewById(R.id.iconsLayout);
        TextView infoTextView = findViewById(R.id.infoTextView);
        TextView bottomTextView = findViewById(R.id.bottomTextView);
        MaterialCardView buttonCardView = findViewById(R.id.buttonCardView);
        Button getStartedButton = findViewById(R.id.getStartedButton);

        // Animation zoom out pour le logo avec bounce
        Animation logoZoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        logoZoomOut.setInterpolator(new OvershootInterpolator());
        logoZoomOut.setDuration(2000);
        logoImageView.startAnimation(logoZoomOut);

        // Animation bounce pour le nom de l'app
        Animation appNameBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        appNameBounce.setStartOffset(600);
        appNameBounce.setDuration(1200);
        appNameTextView.startAnimation(appNameBounce);

        // Animation slide in from right pour "Welcome to"
        Animation welcomeSlideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        welcomeSlideIn.setStartOffset(800);
        welcomeSlideIn.setDuration(1000);
        welcomeTextView.startAnimation(welcomeSlideIn);

        // Animation fade-in pour le sous-titre
        Animation subtitleFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        subtitleFadeIn.setStartOffset(1200);
        subtitleFadeIn.setDuration(1500);
        subtitleTextView.startAnimation(subtitleFadeIn);

        // Animation pour les icônes avec délai séquentiel
        for (int i = 0; i < iconsLayout.getChildCount(); i++) {
            View icon = iconsLayout.getChildAt(i);
            Animation iconBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
            iconBounce.setStartOffset(1600 + (i * 200));
            iconBounce.setDuration(800);
            icon.startAnimation(iconBounce);
        }

        // Animation fade-in pour le texte informatif
        Animation infoFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        infoFadeIn.setStartOffset(2400);
        infoFadeIn.setDuration(1500);
        infoTextView.startAnimation(infoFadeIn);

        // Animation slide up pour le bouton
        Animation buttonSlideUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        buttonSlideUp.setStartOffset(2800);
        buttonSlideUp.setDuration(1200);
        buttonSlideUp.setInterpolator(new DecelerateInterpolator());
        buttonCardView.startAnimation(buttonSlideUp);

        // Animation fade-in pour le texte du bas
        Animation bottomFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        bottomFadeIn.setStartOffset(3000);
        bottomFadeIn.setDuration(1500);
        bottomTextView.startAnimation(bottomFadeIn);

        // Animation de pulsation continue pour le bouton
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulseAnimation.setStartOffset(4000);
        buttonCardView.startAnimation(pulseAnimation);

        // Click listener pour le bouton Get Started avec animation
        getStartedButton.setOnClickListener(v -> {
            // Animation de clic avec scale
            v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .withEndAction(() -> {
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .start();
                    })
                    .start();
        });
    }
}


