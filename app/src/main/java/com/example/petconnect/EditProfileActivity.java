package com.example.petconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petconnect.models.User;
import com.example.petconnect.utils.FirebaseAuthManager;
import com.example.petconnect.utils.ImageStorageManager;
import com.example.petconnect.utils.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private ProgressBar profileCompletionProgressBar;
    private TextView profileCompletionTextView;
    private TextInputLayout fullNameLayout;
    private TextInputLayout usernameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout locationLayout;
    private TextInputLayout bioLayout;
    private Button saveButton;
    private Button cancelButton;
    private Button advancedSettingsButton;
    
    private FirebaseAuthManager authManager;
    private FirebaseUser firebaseUser;
    private User currentUser;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        authManager = FirebaseAuthManager.getInstance();
        userManager = UserManager.getInstance();
        firebaseUser = authManager.getCurrentUser();

        // Setup Toolbar with back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        initializeViews();
        loadUserData();
        setupClickListeners();
        setupBottomNavigation();
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        profileCompletionProgressBar = findViewById(R.id.profileCompletionProgressBar);
        profileCompletionTextView = findViewById(R.id.profileCompletionTextView);
        fullNameLayout = findViewById(R.id.fullNameLayout);
        usernameLayout = findViewById(R.id.usernameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        locationLayout = findViewById(R.id.locationLayout);
        bioLayout = findViewById(R.id.bioLayout);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        advancedSettingsButton = findViewById(R.id.advancedSettingsButton);
    }

    private void loadUserData() {
        if (firebaseUser == null) {
            finish();
            return;
        }

        // Load profile image from Firebase Auth
        if (firebaseUser.getPhotoUrl() != null) {
            Glide.with(this)
                .load(firebaseUser.getPhotoUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(profileImageView);
        }

        // Load email
        if (emailLayout != null && emailLayout.getEditText() != null) {
            emailLayout.getEditText().setText(firebaseUser.getEmail());
        }

        // Load user data from Firestore
        userManager.getUser(firebaseUser.getUid(), new UserManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                
                // Load full name
                if (fullNameLayout != null && fullNameLayout.getEditText() != null) {
                    String fullName = user.getFullName();
                    if (fullName != null && !fullName.isEmpty()) {
                        fullNameLayout.getEditText().setText(fullName);
                    } else if (firebaseUser.getDisplayName() != null) {
                        fullNameLayout.getEditText().setText(firebaseUser.getDisplayName());
                    }
                }

                // Load phone
                if (phoneLayout != null && phoneLayout.getEditText() != null && user.getPhoneNumber() != null) {
                    phoneLayout.getEditText().setText(user.getPhoneNumber());
                }

                // Load address/location
                if (locationLayout != null && locationLayout.getEditText() != null && user.getAddress() != null) {
                    locationLayout.getEditText().setText(user.getAddress());
                }

                // Update profile image if available
                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    Glide.with(EditProfileActivity.this)
                        .load(user.getPhotoUrl())
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(profileImageView);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // If user doesn't exist in Firestore, create one
                if (errorMessage.contains("introuvable")) {
                    createUserDocument();
                } else {
                    // Load from Firebase Auth only
                    if (fullNameLayout != null && fullNameLayout.getEditText() != null && firebaseUser.getDisplayName() != null) {
                        fullNameLayout.getEditText().setText(firebaseUser.getDisplayName());
                    }
                }
            }
        });
    }

    private void createUserDocument() {
        String[] nameParts = firebaseUser.getDisplayName() != null 
            ? firebaseUser.getDisplayName().split(" ", 2) : new String[]{"", ""};
        
        User newUser = new User(
            firebaseUser.getUid(),
            nameParts.length > 0 ? nameParts[0] : "",
            nameParts.length > 1 ? nameParts[1] : "",
            firebaseUser.getEmail()
        );
        newUser.setRole("user");

        userManager.createUser(newUser, new UserManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                if (fullNameLayout != null && fullNameLayout.getEditText() != null) {
                    fullNameLayout.getEditText().setText(user.getFullName());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(EditProfileActivity.this, "Error creating profile: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        // Change photo button
        findViewById(R.id.changePhotoButton).setOnClickListener(v -> showPhotoOptionsDialog());

        // Bimoji button
        findViewById(R.id.bimojiButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, BitmojiBuilderActivity.class);
            startActivity(intent);
        });

        // Save button
        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveProfile());
        }

        // Cancel button
        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> finish());
        }

        // Advanced settings button
        if (advancedSettingsButton != null) {
            advancedSettingsButton.setOnClickListener(v -> {
                // Open advanced settings
                Toast.makeText(this, "Advanced settings coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveProfile() {
        if (firebaseUser == null) return;

        // Validate inputs
        String fullName = fullNameLayout != null && fullNameLayout.getEditText() != null 
            ? fullNameLayout.getEditText().getText().toString().trim() : "";
        String phone = phoneLayout != null && phoneLayout.getEditText() != null 
            ? phoneLayout.getEditText().getText().toString().trim() : "";
        String location = locationLayout != null && locationLayout.getEditText() != null 
            ? locationLayout.getEditText().getText().toString().trim() : "";

        // Validate full name
        if (fullName.isEmpty()) {
            if (fullNameLayout != null) {
                fullNameLayout.setError("Full name is required");
            }
            return;
        }

        // Split full name into first and last
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Get or create user object
        if (currentUser == null) {
            currentUser = new User(firebaseUser.getUid(), firstName, lastName, firebaseUser.getEmail());
            currentUser.setRole("user");
        } else {
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
        }

        // Update fields
        currentUser.setPhoneNumber(phone.isEmpty() ? null : phone);
        currentUser.setAddress(location.isEmpty() ? null : location);

        // Update user in Firestore
        userManager.updateUser(currentUser, new UserManager.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                Toast.makeText(EditProfileActivity.this, "✓ Profile updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EditProfileActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPhotoOptionsDialog() {
        String[] options = {"Camera", "Gallery", "Bimoji"};
        new android.app.AlertDialog.Builder(this)
            .setTitle("Choose Photo Source")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // Camera
                        openCamera();
                        break;
                    case 1: // Gallery
                        openGallery();
                        break;
                    case 2: // Bimoji
                        Intent intent = new Intent(this, BitmojiBuilderActivity.class);
                        startActivity(intent);
                        break;
                }
            })
            .show();
    }

    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 101);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 100) { // Gallery
                android.net.Uri imageUri = data.getData();
                if (imageUri != null) {
                    uploadImage(imageUri, null);
                }
            } else if (requestCode == 101) { // Camera
                android.graphics.Bitmap bitmap = (android.graphics.Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    uploadImage(null, bitmap);
                }
            }
        }
    }

    private void uploadImage(android.net.Uri imageUri, android.graphics.Bitmap bitmap) {
        if (firebaseUser == null) return;

        String userId = firebaseUser.getUid();
        
        // Show progress
        if (saveButton != null) {
            saveButton.setEnabled(false);
            saveButton.setText("Uploading...");
        }

        com.example.petconnect.utils.ImageStorageManager.UploadCallback uploadCallback = 
            new com.example.petconnect.utils.ImageStorageManager.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Update user profile with image URL
                    if (currentUser == null) {
                        currentUser = new User(firebaseUser.getUid(), "", "", firebaseUser.getEmail());
                        currentUser.setRole("user");
                    }
                    currentUser.setPhotoUrl(imageUrl);

                    userManager.updateUser(currentUser, new UserManager.UserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            currentUser = user;
                            // Update image view
                            if (profileImageView != null) {
                                com.bumptech.glide.Glide.with(EditProfileActivity.this)
                                    .load(imageUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_profile)
                                    .into(profileImageView);
                            }
                            if (saveButton != null) {
                                saveButton.setEnabled(true);
                                saveButton.setText("💾 Save");
                            }
                            Toast.makeText(EditProfileActivity.this, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            if (saveButton != null) {
                                saveButton.setEnabled(true);
                                saveButton.setText("💾 Save");
                            }
                            Toast.makeText(EditProfileActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    if (saveButton != null) {
                        saveButton.setEnabled(true);
                        saveButton.setText("💾 Save");
                    }
                    Toast.makeText(EditProfileActivity.this, "Upload error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            };

        if (imageUri != null) {
            com.example.petconnect.utils.ImageStorageManager.getInstance()
                .uploadProfileImage(userId, imageUri, uploadCallback);
        } else if (bitmap != null) {
            com.example.petconnect.utils.ImageStorageManager.getInstance()
                .uploadProfileImageFromBitmap(userId, bitmap, uploadCallback);
        }
    }
}

