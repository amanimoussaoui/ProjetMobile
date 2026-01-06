package com.example.petconnect.modules.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petconnect.R;
import com.example.petconnect.shared.services.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseService.getAuth();
        db = FirebaseService.getDb();

        EditText nameInput = findViewById(R.id.name_input);
        EditText emailInput = findViewById(R.id.email_input);
        Button saveButton = findViewById(R.id.save_profile_button);
        Button logoutButton = findViewById(R.id.logout_button);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        emailInput.setEnabled(false);
        emailInput.setText(currentUser.getEmail());
        loadProfile(currentUser.getUid(), nameInput);

        saveButton.setOnClickListener(v -> saveProfile(currentUser.getUid(), nameInput));
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadProfile(String uid, EditText nameInput) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String name = snapshot.getString("name");
                    nameInput.setText(name);
                }
            })
            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveProfile(String uid, EditText nameInput) {
        String name = nameInput.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Nom requis", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        db.collection("users").document(uid).set(data)
            .addOnSuccessListener(unused -> Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
