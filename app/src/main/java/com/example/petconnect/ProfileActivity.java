package com.example.petconnect;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.petconnect.models.User;
import com.example.petconnect.utils.AvatarBuilder;
import com.example.petconnect.utils.FirebaseAuthManager;
import com.example.petconnect.utils.ImageStorageManager;
import com.example.petconnect.utils.InputValidator;
import com.example.petconnect.utils.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView profileImageView;
    private ProgressBar progressBar;
    private FirebaseAuthManager authManager;
    private UserManager userManager;
    private User currentUser;
    private FirebaseUser firebaseUser;
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_IMAGE_PICK = 101;
    private static final int CAMERA_PERMISSION_CODE = 200;
    private Bitmap selectedImageBitmap;
    private Uri selectedImageUri;
    
    // ActivityResultLaunchers pour Android 13+
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestStoragePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authManager = FirebaseAuthManager.getInstance();
        userManager = UserManager.getInstance();

        // Vérifier si l'utilisateur est connecté
        firebaseUser = authManager.getCurrentUser();
        if (firebaseUser == null) {
            // Rediriger vers LoginActivity si non connecté
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        profileImageView = findViewById(R.id.profileImageView);
        progressBar = findViewById(R.id.progressBar);

        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        // Bouton Edit Profile
        Button editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
        
        // Bouton Chatbot
        Button chatbotButton = findViewById(R.id.chatbotButton);
        if (chatbotButton != null) {
            chatbotButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, ChatbotActivity.class);
                startActivity(intent);
            });
        }

        // Bouton Logout
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Charger les données de l'utilisateur
        loadUserData();
        
        // Initialiser les ActivityResultLaunchers
        initializeActivityResultLaunchers();
    }
    
    private void initializeActivityResultLaunchers() {
        // Launcher pour la sélection d'image depuis la galerie
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedImageUri = imageUri;
                            try {
                                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                updateProfileImageInDialog(selectedImageBitmap);
                            } catch (IOException e) {
                                Toast.makeText(ProfileActivity.this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        );
        
        // Launcher pour la caméra
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        if (photo != null) {
                            selectedImageBitmap = photo;
                            updateProfileImageInDialog(photo);
                        }
                    }
                }
            }
        );
        
        // Launcher pour la permission caméra
        requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show();
                }
            }
        );
        
        // Launcher pour la permission de stockage/galerie
        requestStoragePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openImagePickerAfterPermission();
                } else {
                    Toast.makeText(this, "Permission galerie refusée", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void loadUserData() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        userManager.getUser(firebaseUser.getUid(), new UserManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                updateProfileInfo();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                // Si l'utilisateur n'existe pas dans Firestore, créer un document avec les données de base
                if (errorMessage.contains("introuvable")) {
                    createUserDocument();
                } else {
                    Toast.makeText(ProfileActivity.this, "Erreur: " + errorMessage, Toast.LENGTH_SHORT).show();
                    // Afficher quand même les données de Firebase Auth
                    updateProfileInfoFromAuth();
                }
            }
        });
    }

    private void createUserDocument() {
        User newUser = new User(
                firebaseUser.getUid(),
                firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName().split(" ")[0] : "",
                firebaseUser.getDisplayName() != null && firebaseUser.getDisplayName().split(" ").length > 1 
                    ? firebaseUser.getDisplayName().split(" ", 2)[1] : "",
                firebaseUser.getEmail()
        );
        newUser.setRole("user");

        userManager.createUser(newUser, new UserManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                updateProfileInfo();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProfileActivity.this, "Erreur lors de la création du profil: " + errorMessage, Toast.LENGTH_SHORT).show();
                updateProfileInfoFromAuth();
            }
        });
    }

    private void updateProfileInfo() {
        if (currentUser != null) {
            nameTextView.setText(currentUser.getFullName());
            emailTextView.setText(currentUser.getEmail());
            
            // Charger l'image de profil si elle existe
            if (currentUser.getPhotoUrl() != null && !currentUser.getPhotoUrl().isEmpty()) {
                Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile);
            }
        } else {
            updateProfileInfoFromAuth();
        }
    }

    private void updateProfileInfoFromAuth() {
        if (firebaseUser != null) {
            String displayName = firebaseUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                nameTextView.setText(displayName);
            } else {
                nameTextView.setText("Utilisateur");
            }
            emailTextView.setText(firebaseUser.getEmail());
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        ImageView profileImageDialogView = dialogView.findViewById(R.id.profileImageDialogView);
        Button changePhotoButton = dialogView.findViewById(R.id.changePhotoButton);
        Button createAvatarButton = dialogView.findViewById(R.id.createAvatarButton);
        
        // Utiliser TextInputLayout au lieu de EditText directement
        com.google.android.material.textfield.TextInputLayout firstNameLayout = dialogView.findViewById(R.id.firstNameLayout);
        com.google.android.material.textfield.TextInputLayout lastNameLayout = dialogView.findViewById(R.id.lastNameLayout);
        com.google.android.material.textfield.TextInputLayout emailLayout = dialogView.findViewById(R.id.emailLayout);
        com.google.android.material.textfield.TextInputLayout phoneLayout = dialogView.findViewById(R.id.phoneLayout);
        com.google.android.material.textfield.TextInputLayout addressLayout = dialogView.findViewById(R.id.addressLayout);
        
        EditText firstNameEditText = firstNameLayout != null ? firstNameLayout.getEditText() : null;
        EditText lastNameEditText = lastNameLayout != null ? lastNameLayout.getEditText() : null;
        EditText emailEditText = emailLayout != null ? emailLayout.getEditText() : null;
        EditText phoneEditText = phoneLayout != null ? phoneLayout.getEditText() : null;
        EditText addressEditText = addressLayout != null ? addressLayout.getEditText() : null;

        // Afficher l'image de profil actuelle
        if (currentUser != null && currentUser.getPhotoUrl() != null && !currentUser.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(profileImageDialogView);
        }

        changePhotoButton.setOnClickListener(v -> {
            showImageSourceDialog();
            // Fermer le dialogue temporairement pour prendre la photo
        });

        // Pré-remplir les champs
        if (currentUser != null) {
            firstNameEditText.setText(currentUser.getFirstName());
            lastNameEditText.setText(currentUser.getLastName());
            emailEditText.setText(currentUser.getEmail());
            if (currentUser.getAddress() != null) {
                addressEditText.setText(currentUser.getAddress());
            }
            if (currentUser.getPhoneNumber() != null) {
                phoneEditText.setText(currentUser.getPhoneNumber());
            }
        } else if (firebaseUser != null) {
            String displayName = firebaseUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                String[] nameParts = displayName.split(" ", 2);
                firstNameEditText.setText(nameParts[0]);
                if (nameParts.length > 1) {
                    lastNameEditText.setText(nameParts[1]);
                }
            }
            emailEditText.setText(firebaseUser.getEmail());
        }

        AlertDialog dialog = builder.create();

        changePhotoButton.setOnClickListener(v -> {
            dialog.dismiss();
            showImageSourceDialog();
        });
        
        // Bouton pour créer un avatar personnalisé - ouvre l'activité Bitmoji
        createAvatarButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ProfileActivity.this, BitmojiBuilderActivity.class);
            startActivity(intent);
        });

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.save), (d, which) -> {
            if (firstNameEditText == null || lastNameEditText == null) {
                return;
            }
            
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String address = addressEditText != null ? addressEditText.getText().toString().trim() : "";
            String phone = phoneEditText != null ? phoneEditText.getText().toString().trim() : "";

            // Validation avec InputValidator
            boolean isValid = true;
            
            InputValidator.ValidationResult firstNameResult = InputValidator.validateName(firstName);
            if (!firstNameResult.isValid()) {
                if (firstNameLayout != null) {
                    firstNameLayout.setError(firstNameResult.getErrorMessage());
                }
                isValid = false;
            } else {
                if (firstNameLayout != null) {
                    firstNameLayout.setError(null);
                }
            }
            
            InputValidator.ValidationResult lastNameResult = InputValidator.validateName(lastName);
            if (!lastNameResult.isValid()) {
                if (lastNameLayout != null) {
                    lastNameLayout.setError(lastNameResult.getErrorMessage());
                }
                isValid = false;
            } else {
                if (lastNameLayout != null) {
                    lastNameLayout.setError(null);
                }
            }
            
            if (phone != null && !phone.isEmpty()) {
                InputValidator.ValidationResult phoneResult = InputValidator.validatePhone(phone);
                if (!phoneResult.isValid()) {
                    if (phoneLayout != null) {
                        phoneLayout.setError(phoneResult.getErrorMessage());
                    }
                    isValid = false;
                } else {
                    if (phoneLayout != null) {
                        phoneLayout.setError(null);
                    }
                }
            }

            if (!isValid) {
                Toast.makeText(this, "Veuillez corriger les erreurs", Toast.LENGTH_SHORT).show();
                return;
            }

            saveProfile(firstName, lastName, address, phone);
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (d, which) -> dialog.dismiss());

        dialog.show();
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
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Cette méthode est conservée pour la compatibilité avec les anciennes versions
        // mais les permissions sont maintenant gérées par ActivityResultLauncher
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(this, "Aucune application caméra disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        // Vérifier les permissions pour Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ : Utiliser READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                return;
            }
        } else {
            // Android 12 et inférieur : Utiliser READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                return;
            }
        }
        
        openImagePickerAfterPermission();
    }
    
    private void openImagePickerAfterPermission() {
        // Utiliser ACTION_GET_CONTENT pour une meilleure compatibilité
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        // Créer un Intent chooser
        Intent chooserIntent = Intent.createChooser(intent, "Sélectionner une image");
        
        try {
            imagePickerLauncher.launch(chooserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'ouverture de la galerie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                if (selectedImageBitmap != null) {
                    updateProfileImageInDialog(selectedImageBitmap);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                selectedImageUri = data.getData();
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    updateProfileImageInDialog(selectedImageBitmap);
                } catch (IOException e) {
                    Toast.makeText(this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateProfileImageInDialog(Bitmap bitmap) {
        // Mettre à jour l'image dans le dialogue si ouvert
        // Sinon, elle sera sauvegardée lors de la sauvegarde du profil
        if (profileImageView != null) {
            profileImageView.setImageBitmap(bitmap);
        }
    }

    private void saveProfile(String firstName, String lastName, String address, String phone) {
        Uri imageUri = selectedImageUri;
        Bitmap imageBitmap = selectedImageBitmap;
        if (currentUser == null) {
            currentUser = new User(firebaseUser.getUid(), firstName, lastName, firebaseUser.getEmail());
            currentUser.setRole("user");
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setAddress(address.isEmpty() ? null : address);
        currentUser.setPhoneNumber(phone.isEmpty() ? null : phone);

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Si une nouvelle image a été sélectionnée, l'uploader d'abord
        if (imageUri != null || imageBitmap != null) {
            uploadProfileImage(imageUri, imageBitmap, firstName, lastName, address, phone);
        } else {
            // Sinon, sauvegarder directement
            userManager.updateUser(currentUser, new UserManager.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    currentUser = user;
                    updateProfileInfo();
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(ProfileActivity.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(ProfileActivity.this, "Erreur: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadProfileImage(Uri imageUri, Bitmap imageBitmap, String firstName, String lastName, String address, String phone) {
        String userId = firebaseUser.getUid();
        
        ImageStorageManager.UploadCallback uploadCallback = new ImageStorageManager.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                currentUser.setPhotoUrl(imageUrl);
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                currentUser.setAddress(address.isEmpty() ? null : address);
                currentUser.setPhoneNumber(phone.isEmpty() ? null : phone);

                userManager.updateUser(currentUser, new UserManager.UserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        currentUser = user;
                        updateProfileInfo();
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(ProfileActivity.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                        // Réinitialiser les variables d'image
                        selectedImageUri = null;
                        selectedImageBitmap = null;
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        Toast.makeText(ProfileActivity.this, "Erreur lors de la sauvegarde: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(ProfileActivity.this, "Erreur lors de l'upload de l'image: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        };

        if (imageUri != null) {
            ImageStorageManager.getInstance().uploadProfileImage(userId, imageUri, uploadCallback);
        } else if (imageBitmap != null) {
            ImageStorageManager.getInstance().uploadProfileImageFromBitmap(userId, imageBitmap, uploadCallback);
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.logout, (dialog, which) -> {
                    authManager.logout();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void createCustomAvatar(ImageView imageView) {
        if (firebaseUser == null) {
            return;
        }
        
        // Générer un avatar basé sur l'ID utilisateur
        String seed = firebaseUser.getUid();
        Bitmap avatarBitmap = AvatarBuilder.createAvatar(seed, 400, 400);
        
        // Afficher l'avatar
        if (imageView != null) {
            imageView.setImageBitmap(avatarBitmap);
        }
        
        // Sauvegarder l'avatar comme image de profil
        selectedImageBitmap = avatarBitmap;
        
        // Sauvegarder les données d'avatar dans le profil utilisateur
        Map<String, String> avatarPreferences = new HashMap<>();
        avatarPreferences.put("style", "bitmoji");
        avatarPreferences.put("seed", seed);
        String avatarData = AvatarBuilder.saveAvatarData(seed, avatarPreferences);
        
        if (currentUser == null) {
            currentUser = new User(firebaseUser.getUid(), 
                firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName().split(" ")[0] : "",
                firebaseUser.getDisplayName() != null && firebaseUser.getDisplayName().split(" ").length > 1 
                    ? firebaseUser.getDisplayName().split(" ", 2)[1] : "",
                firebaseUser.getEmail());
            currentUser.setRole("user");
        }
        
        currentUser.setAvatarData(avatarData);
        
        Toast.makeText(this, "Avatar créé ! N'oubliez pas de sauvegarder votre profil.", Toast.LENGTH_LONG).show();
    }
}


