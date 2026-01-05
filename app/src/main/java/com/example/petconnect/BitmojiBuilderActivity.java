package com.example.petconnect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petconnect.utils.AvatarBuilder;
import com.example.petconnect.utils.FirebaseAuthManager;
import com.example.petconnect.utils.ImageStorageManager;
import com.example.petconnect.utils.UserManager;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Activité pour créer un avatar Bitmoji personnalisé
 */
public class BitmojiBuilderActivity extends AppCompatActivity {
    
    private ImageView avatarPreview;
    private SeekBar skinToneSeekBar;
    private SeekBar hairColorSeekBar;
    private SeekBar eyeColorSeekBar;
    private Button saveButton;
    private Button generateButton;
    
    private Bitmap currentAvatar;
    private int skinTone = 0;
    private int hairColor = 0;
    private int eyeColor = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmoji_builder);
        
        avatarPreview = findViewById(R.id.avatarPreview);
        skinToneSeekBar = findViewById(R.id.skinToneSeekBar);
        hairColorSeekBar = findViewById(R.id.hairColorSeekBar);
        eyeColorSeekBar = findViewById(R.id.eyeColorSeekBar);
        saveButton = findViewById(R.id.saveAvatarButton);
        generateButton = findViewById(R.id.generateAvatarButton);
        
        // Vérifier que le bouton est trouvé
        if (saveButton == null) {
            Toast.makeText(this, "Erreur: Bouton de sauvegarde introuvable", Toast.LENGTH_LONG).show();
            android.util.Log.e("BitmojiBuilder", "saveAvatarButton not found in layout");
        }
        
        TextView skinToneLabel = findViewById(R.id.skinToneLabel);
        TextView hairColorLabel = findViewById(R.id.hairColorLabel);
        TextView eyeColorLabel = findViewById(R.id.eyeColorLabel);
        
        // Configuration des SeekBars
        skinToneSeekBar.setMax(5);
        hairColorSeekBar.setMax(7);
        eyeColorSeekBar.setMax(5);
        
        skinToneSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                skinTone = progress;
                updateAvatar();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        hairColorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hairColor = progress;
                updateAvatar();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        eyeColorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                eyeColor = progress;
                updateAvatar();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        generateButton.setOnClickListener(v -> generateNewAvatar());
        saveButton.setOnClickListener(v -> saveAvatar());
        
        // Générer un avatar initial
        generateNewAvatar();
    }
    
    private void generateNewAvatar() {
        FirebaseUser user = FirebaseAuthManager.getInstance().getCurrentUser();
        String seed = user != null ? user.getUid() : "default";
        
        // Générer un avatar avec les paramètres actuels
        currentAvatar = createCustomAvatar(seed, 400, 400);
        updateAvatar();
    }
    
    private void updateAvatar() {
        if (currentAvatar != null) {
            // Recréer l'avatar avec les nouveaux paramètres
            FirebaseUser user = FirebaseAuthManager.getInstance().getCurrentUser();
            String seed = user != null ? user.getUid() : "default";
            currentAvatar = createCustomAvatar(seed, 400, 400);
            avatarPreview.setImageBitmap(currentAvatar);
        }
    }
    
    private Bitmap createCustomAvatar(String seed, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Couleurs de peau
        int[] skinTones = {
            0xFFFFDBB3, // Clair
            0xFFFFD4A3,
            0xFFFFC893,
            0xFFFFB883,
            0xFFFFA873,
            0xFFE89B63  // Foncé
        };
        
        // Couleurs de cheveux
        int[] hairColors = {
            0xFF8B4513, // Brun
            0xFF000000, // Noir
            0xFFFFD700, // Blond
            0xFFA0522D, // Châtain
            0xFFDC143C, // Rouge
            0xFF4B0082, // Violet
            0xFF808080  // Gris
        };
        
        // Couleurs d'yeux
        int[] eyeColors = {
            0xFF000000, // Noir
            0xFF8B4513, // Marron
            0xFF4169E1, // Bleu
            0xFF228B22, // Vert
            0xFF9370DB, // Violet
            0xFFDC143C  // Rouge
        };
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        // Fond
        paint.setColor(0xFFFFFFFF);
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, paint);
        
        // Visage
        paint.setColor(skinTones[Math.min(skinTone, skinTones.length - 1)]);
        float faceRadius = width * 0.35f;
        canvas.drawCircle(width / 2f, height / 2f + height * 0.1f, faceRadius, paint);
        
        // Cheveux
        paint.setColor(hairColors[Math.min(hairColor, hairColors.length - 1)]);
        RectF hairRect = new RectF(
            width * 0.2f,
            height * 0.15f,
            width * 0.8f,
            height * 0.5f
        );
        canvas.drawOval(hairRect, paint);
        
        // Yeux
        paint.setColor(eyeColors[Math.min(eyeColor, eyeColors.length - 1)]);
        float eyeSize = width * 0.08f;
        float eyeY = height * 0.45f;
        canvas.drawCircle(width * 0.4f, eyeY, eyeSize, paint);
        canvas.drawCircle(width * 0.6f, eyeY, eyeSize, paint);
        
        // Bouche
        paint.setColor(0xFF000000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width * 0.02f);
        RectF mouthRect = new RectF(
            width * 0.4f,
            height * 0.6f,
            width * 0.6f,
            height * 0.65f
        );
        canvas.drawArc(mouthRect, 0, 180, false, paint);
        
        return bitmap;
    }
    
    private void saveAvatar() {
        android.util.Log.d("BitmojiBuilder", "saveAvatar() called");
        
        if (currentAvatar == null) {
            android.util.Log.e("BitmojiBuilder", "currentAvatar is null");
            Toast.makeText(this, "Aucun avatar à sauvegarder. Veuillez générer un avatar d'abord.", Toast.LENGTH_LONG).show();
            return;
        }
        
        FirebaseUser user = FirebaseAuthManager.getInstance().getCurrentUser();
        if (user == null) {
            android.util.Log.e("BitmojiBuilder", "User is null");
            Toast.makeText(this, "Utilisateur non connecté. Veuillez vous reconnecter.", Toast.LENGTH_LONG).show();
            return;
        }
        
        String userId = user.getUid();
        android.util.Log.d("BitmojiBuilder", "Saving avatar for user: " + userId);
        
        // Désactiver le bouton pendant la sauvegarde
        if (saveButton != null) {
            saveButton.setEnabled(false);
            saveButton.setText("Sauvegarde...");
        } else {
            android.util.Log.e("BitmojiBuilder", "saveButton is null!");
            Toast.makeText(this, "Erreur: Bouton introuvable", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Sauvegarder les paramètres
        Map<String, String> preferences = new HashMap<>();
        preferences.put("skinTone", String.valueOf(skinTone));
        preferences.put("hairColor", String.valueOf(hairColor));
        preferences.put("eyeColor", String.valueOf(eyeColor));
        preferences.put("seed", userId);
        final String avatarData = AvatarBuilder.saveAvatarData(userId, preferences);
        
        // Uploader l'image
        android.util.Log.d("BitmojiBuilder", "Starting image upload...");
        ImageStorageManager.getInstance().uploadProfileImageFromBitmap(userId, currentAvatar, 
            new ImageStorageManager.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    android.util.Log.d("BitmojiBuilder", "Image uploaded successfully: " + imageUrl);
                    // Mettre à jour le profil utilisateur
                    UserManager.getInstance().getUser(userId, new UserManager.UserCallback() {
                        @Override
                        public void onSuccess(com.example.petconnect.models.User user) {
                            // Mettre à jour les données
                            user.setPhotoUrl(imageUrl);
                            user.setAvatarData(avatarData);
                            
                            // Sauvegarder dans Firestore
                            UserManager.getInstance().updateUser(user, new UserManager.UserCallback() {
                                @Override
                                public void onSuccess(com.example.petconnect.models.User updatedUser) {
                                    runOnUiThread(() -> {
                                        if (saveButton != null) {
                                            saveButton.setEnabled(true);
                                            saveButton.setText("Sauvegarder l'avatar");
                                        }
                                        Toast.makeText(BitmojiBuilderActivity.this, 
                                            "✓ Avatar sauvegardé avec succès !", Toast.LENGTH_SHORT).show();
                                        android.util.Log.d("BitmojiBuilder", "Avatar saved successfully, finishing activity");
                                        finish();
                                    });
                                }
                                
                                @Override
                                public void onError(String errorMessage) {
                                    runOnUiThread(() -> {
                                        if (saveButton != null) {
                                            saveButton.setEnabled(true);
                                            saveButton.setText("Sauvegarder");
                                        }
                                        Toast.makeText(BitmojiBuilderActivity.this, 
                                            "Erreur lors de la sauvegarde: " + errorMessage, Toast.LENGTH_LONG).show();
                                    });
                                }
                            });
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            // Si l'utilisateur n'existe pas, le créer
                            FirebaseUser firebaseUser = FirebaseAuthManager.getInstance().getCurrentUser();
                            if (firebaseUser != null && (errorMessage.contains("introuvable") || errorMessage.contains("not found"))) {
                                com.example.petconnect.models.User newUser = new com.example.petconnect.models.User(
                                    userId,
                                    firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName().split(" ")[0] : "",
                                    firebaseUser.getDisplayName() != null && firebaseUser.getDisplayName().split(" ").length > 1 
                                        ? firebaseUser.getDisplayName().split(" ", 2)[1] : "",
                                    firebaseUser.getEmail()
                                );
                                newUser.setRole("user");
                                newUser.setPhotoUrl(imageUrl);
                                newUser.setAvatarData(avatarData);
                                
                                UserManager.getInstance().createUser(newUser, new UserManager.UserCallback() {
                                    @Override
                                    public void onSuccess(com.example.petconnect.models.User createdUser) {
                                        runOnUiThread(() -> {
                                            if (saveButton != null) {
                                                saveButton.setEnabled(true);
                                                saveButton.setText("Sauvegarder");
                                            }
                                            Toast.makeText(BitmojiBuilderActivity.this, 
                                                "Avatar sauvegardé avec succès !", Toast.LENGTH_SHORT).show();
                                            finish();
                                        });
                                    }
                                    
                                    @Override
                                    public void onError(String createError) {
                                        runOnUiThread(() -> {
                                            if (saveButton != null) {
                                                saveButton.setEnabled(true);
                                                saveButton.setText("Sauvegarder");
                                            }
                                            Toast.makeText(BitmojiBuilderActivity.this, 
                                                "Erreur lors de la création du profil: " + createError, Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(() -> {
                                    if (saveButton != null) {
                                        saveButton.setEnabled(true);
                                        saveButton.setText("Sauvegarder");
                                    }
                                    Toast.makeText(BitmojiBuilderActivity.this, 
                                        "Erreur: " + errorMessage, Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    android.util.Log.e("BitmojiBuilder", "Image upload error: " + errorMessage);
                    runOnUiThread(() -> {
                        if (saveButton != null) {
                            saveButton.setEnabled(true);
                            saveButton.setText("Sauvegarder l'avatar");
                        }
                        Toast.makeText(BitmojiBuilderActivity.this, 
                            "Erreur lors de l'upload: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });
    }
}

