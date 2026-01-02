package com.example.petconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.petconnect.utils.EmailService;
import com.example.petconnect.utils.ImageStorageManager;
import com.example.petconnect.utils.LoginAttemptManager;

import java.io.IOException;

public class PhotoVerificationActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_IMAGE_PICK = 101;
    private static final int CAMERA_PERMISSION_CODE = 200;
    
    private ImageView photoImageView;
    private Button takePhotoButton;
    private Button submitButton;
    private ProgressBar progressBar;
    private Bitmap capturedBitmap;
    private String userEmail;
    private LoginAttemptManager attemptManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_verification);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Email non fourni", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        attemptManager = new LoginAttemptManager(this);

        photoImageView = findViewById(R.id.photoImageView);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        takePhotoButton.setOnClickListener(v -> showImageSourceDialog());
        submitButton.setOnClickListener(v -> submitVerificationPhoto());
        submitButton.setEnabled(false);
        
        // Ouvrir automatiquement la caméra au démarrage
        requestCameraPermission();
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisir la source de l'image");
        builder.setItems(new String[]{"Caméra", "Galerie"}, (dialog, which) -> {
            if (which == 0) {
                requestCameraPermission();
            } else {
                openImagePicker();
            }
        });
        builder.show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Vérifier qu'une application peut gérer cet intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                // Si aucune caméra n'est disponible, proposer la galerie
                Toast.makeText(this, "Caméra non disponible, ouverture de la galerie...", Toast.LENGTH_SHORT).show();
                openImagePicker();
            }
        } catch (Exception e) {
            // En cas d'erreur, proposer la galerie
            Toast.makeText(this, "Erreur d'accès à la caméra, ouverture de la galerie...", Toast.LENGTH_SHORT).show();
            openImagePicker();
        }
    }

    private void openImagePicker() {
        // Utiliser ACTION_GET_CONTENT pour une meilleure compatibilité avec Android 13+
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        // Essayer d'ouvrir avec ACTION_PICK d'abord
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        
        // Créer un Intent chooser pour donner le choix à l'utilisateur
        Intent chooserIntent = Intent.createChooser(intent, "Sélectionner une image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        
        try {
            startActivityForResult(chooserIntent, REQUEST_IMAGE_PICK);
        } catch (Exception e) {
            // Fallback vers l'ancienne méthode
            Intent fallbackIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            fallbackIntent.setType("image/*");
            startActivityForResult(fallbackIntent, REQUEST_IMAGE_PICK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                capturedBitmap = (Bitmap) data.getExtras().get("data");
                if (capturedBitmap != null) {
                    photoImageView.setImageBitmap(capturedBitmap);
                    submitButton.setEnabled(true);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri imageUri = data.getData();
                try {
                    capturedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    photoImageView.setImageBitmap(capturedBitmap);
                    submitButton.setEnabled(true);
                } catch (IOException e) {
                    Toast.makeText(this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void submitVerificationPhoto() {
        if (capturedBitmap == null) {
            Toast.makeText(this, "Veuillez prendre ou sélectionner une photo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(android.view.View.VISIBLE);
        }
        submitButton.setEnabled(false);
        takePhotoButton.setEnabled(false);

        ImageStorageManager.getInstance().uploadVerificationImageFromBitmap(
            userEmail, 
            capturedBitmap, 
            new ImageStorageManager.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Envoyer l'email avec l'image
                    sendVerificationEmail(imageUrl);
                }

                @Override
                public void onError(String errorMessage) {
                    if (progressBar != null) {
                        progressBar.setVisibility(android.view.View.GONE);
                    }
                    submitButton.setEnabled(true);
                    takePhotoButton.setEnabled(true);
                    Toast.makeText(PhotoVerificationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void sendVerificationEmail(String imageUrl) {
        EmailService.getInstance().sendVerificationPhotoEmail(
            userEmail,
            imageUrl,
            new EmailService.EmailCallback() {
                @Override
                public void onSuccess() {
                    if (progressBar != null) {
                        progressBar.setVisibility(android.view.View.GONE);
                    }
                    
                    // Réinitialiser les tentatives de connexion après envoi de la photo
                    attemptManager.resetAttempts(userEmail);
                    
                    Toast.makeText(PhotoVerificationActivity.this, 
                        "Photo de vérification envoyée. Un email sera envoyé à " + userEmail, 
                        Toast.LENGTH_LONG).show();
                    
                    // Retourner à l'écran de connexion
                    Intent intent = new Intent(PhotoVerificationActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    if (progressBar != null) {
                        progressBar.setVisibility(android.view.View.GONE);
                    }
                    
                    // L'image est déjà uploadée, informer l'utilisateur
                    Toast.makeText(PhotoVerificationActivity.this, 
                        "Photo sauvegardée. Envoi d'email en cours: " + errorMessage, 
                        Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        );
    }
}

