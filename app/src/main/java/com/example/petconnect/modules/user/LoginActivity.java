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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseService.getAuth();
        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        Button loginButton = findViewById(R.id.login_button);
        TextView linkRegister = findViewById(R.id.link_register);
        TextView linkForgotPassword = findViewById(R.id.link_forgot_password);

        if (auth.getCurrentUser() != null) {
            goToHome();
            return;
        }

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Email et mot de passe requis", Toast.LENGTH_SHORT).show();
                return;
            }
            loginButton.setEnabled(false);
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    loginButton.setEnabled(true);
                    goToHome();
                })
                .addOnFailureListener(e -> {
                    loginButton.setEnabled(true);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        });

        linkRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        linkForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
            finish();
        });
    }

    private void goToHome() {
        startActivity(new Intent(this, com.example.petconnect.MainActivity.class));
        finish();
    }
}
