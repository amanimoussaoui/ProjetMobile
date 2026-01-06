package com.example.petconnect.modules.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petconnect.R;
import com.example.petconnect.shared.services.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseService.getAuth();
        EditText emailInput = findViewById(R.id.email_input);
        Button resetButton = findViewById(R.id.reset_button);
        TextView backToLogin = findViewById(R.id.back_to_login);

        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Veuillez entrer un email", Toast.LENGTH_SHORT).show();
                return;
            }
            resetButton.setEnabled(false);
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    resetButton.setEnabled(true);
                    Toast.makeText(this, "Lien de réinitialisation envoyé à " + email, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    resetButton.setEnabled(true);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        });

        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
