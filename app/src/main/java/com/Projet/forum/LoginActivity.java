package com.Projet.forum;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.Projet.forum.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final String TAG = "LoginActivity";

    private boolean isRegisterMode = false; // tracks mode

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // LOGIN
        binding.btnLogin.setOnClickListener(v -> loginUser());

        // REGISTER
        binding.btnRegister.setOnClickListener(v -> {
            if (!isRegisterMode) {
                // First click: show name field, change button text
                binding.editName.setVisibility(View.VISIBLE);
                binding.btnRegister.setText("Confirmer inscription");
                isRegisterMode = true;
            } else {
                // Second click: actually register
                registerUser();
            }
        });
    }



    /* =========================
       LOGIN
       ========================= */
    private void loginUser() {
        String email = binding.editEmail.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnLogin.setEnabled(false);
        Log.d(TAG, "Logging in user: " + email);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser == null) {
                        binding.btnLogin.setEnabled(true);
                        return;
                    }

                    String uid = firebaseUser.getUid();

                    db.collection("users").document(uid).get()
                            .addOnSuccessListener(doc -> {
                                if (!doc.exists()) {
                                    Toast.makeText(this, "Profil utilisateur introuvable", Toast.LENGTH_SHORT).show();
                                    binding.btnLogin.setEnabled(true);
                                    return;
                                }

                                User user = doc.toObject(User.class);
                                FirebaseStorageHelper.getInstance().setCurrentUser(user);

                                startActivity(new Intent(LoginActivity.this, ForumListActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                binding.btnLogin.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Échec de connexion : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.btnLogin.setEnabled(true);
                });
    }

    /* =========================
       REGISTER
       ========================= */
    private void registerUser() {
        String email = binding.editEmail.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        String name = binding.editName.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnRegister.setEnabled(false);
        Log.d("RegisterDebug", "Starting registration for: " + email);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser == null) {
                        binding.btnRegister.setEnabled(true);
                        return;
                    }

                    String uid = firebaseUser.getUid();

                    // Create user object without storing password in Firestore
                    User user = new User(uid, email, name, false);

                    // Save to Firestore asynchronously
                    FirebaseStorageHelper.getInstance().addUser(user, success -> {
                        if (success) {
                            Log.d("RegisterDebug", "User added to Firestore successfully");

                            // Set current user
                            FirebaseStorageHelper.getInstance().setCurrentUser(user);
                            Log.d("RegisterDebug", "Current user set");

                            Toast.makeText(LoginActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

                            // Redirect to ForumListActivity safely
                            startActivity(new Intent(LoginActivity.this, ForumListActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Erreur Firestore lors de l'ajout de l'utilisateur", Toast.LENGTH_SHORT).show();
                            binding.btnRegister.setEnabled(true);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Erreur Auth : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.btnRegister.setEnabled(true);
                });
    }


}
