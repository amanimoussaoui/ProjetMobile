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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseService.getAuth();
        db = FirebaseService.getDb();

        EditText nameInput = findViewById(R.id.name_input);
        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        Button registerButton = findViewById(R.id.register_button);
        TextView linkLogin = findViewById(R.id.link_login);

        if (auth.getCurrentUser() != null) {
            goToHome();
            return;
        }

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Nom, email et mot de passe requis", Toast.LENGTH_SHORT).show();
                return;
            }
            registerButton.setEnabled(false);
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    Map<String, Object> userDoc = new HashMap<>();
                    userDoc.put("name", name);
                    userDoc.put("email", email);
                    db.collection("users").document(uid).set(userDoc)
                        .addOnSuccessListener(unused -> {
                            registerButton.setEnabled(true);
                            goToHome();
                        })
                        .addOnFailureListener(e -> {
                            registerButton.setEnabled(true);
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                })
                .addOnFailureListener(e -> {
                    registerButton.setEnabled(true);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        });

        linkLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void goToHome() {
        startActivity(new Intent(this, com.example.petconnect.MainActivity.class));
        finish();
    }
}
