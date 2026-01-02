package com.example.petconnect.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ImageStorageManager {
    private static final String TAG = "ImageStorageManager";
    private static final String PROFILE_IMAGES_FOLDER = "profile_images";
    private static final String VERIFICATION_IMAGES_FOLDER = "verification_images";
    private static ImageStorageManager instance;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private ImageStorageManager() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public static synchronized ImageStorageManager getInstance() {
        if (instance == null) {
            instance = new ImageStorageManager();
        }
        return instance;
    }

    public void uploadProfileImage(String userId, Uri imageUri, UploadCallback callback) {
        StorageReference profileImagesRef = storageRef.child(PROFILE_IMAGES_FOLDER);
        StorageReference userImageRef = profileImagesRef.child(userId + ".jpg");

        UploadTask uploadTask = userImageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "Profile image uploaded successfully: " + uri.toString());
                callback.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting download URL", e);
                callback.onError("Erreur lors de la récupération de l'URL de l'image");
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error uploading profile image", e);
            callback.onError("Erreur lors de l'upload de l'image: " + e.getMessage());
        });
    }

    public void uploadProfileImageFromBitmap(String userId, Bitmap bitmap, UploadCallback callback) {
        StorageReference profileImagesRef = storageRef.child(PROFILE_IMAGES_FOLDER);
        StorageReference userImageRef = profileImagesRef.child(userId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = userImageRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "Profile image uploaded successfully: " + uri.toString());
                callback.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting download URL", e);
                callback.onError("Erreur lors de la récupération de l'URL de l'image");
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error uploading profile image", e);
            callback.onError("Erreur lors de l'upload de l'image: " + e.getMessage());
        });
    }

    public void uploadVerificationImage(String email, Uri imageUri, UploadCallback callback) {
        String sanitizedEmail = email.replaceAll("[^a-zA-Z0-9]", "_");
        StorageReference verificationImagesRef = storageRef.child(VERIFICATION_IMAGES_FOLDER);
        StorageReference imageRef = verificationImagesRef.child(sanitizedEmail + "_" + System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "Verification image uploaded successfully: " + uri.toString());
                callback.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting download URL", e);
                callback.onError("Erreur lors de la récupération de l'URL de l'image");
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error uploading verification image", e);
            callback.onError("Erreur lors de l'upload de l'image: " + e.getMessage());
        });
    }

    public void uploadVerificationImageFromBitmap(String email, Bitmap bitmap, UploadCallback callback) {
        String sanitizedEmail = email.replaceAll("[^a-zA-Z0-9]", "_");
        StorageReference verificationImagesRef = storageRef.child(VERIFICATION_IMAGES_FOLDER);
        StorageReference imageRef = verificationImagesRef.child(sanitizedEmail + "_" + System.currentTimeMillis() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "Verification image uploaded successfully: " + uri.toString());
                callback.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting download URL", e);
                callback.onError("Erreur lors de la récupération de l'URL de l'image");
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error uploading verification image", e);
            callback.onError("Erreur lors de l'upload de l'image: " + e.getMessage());
        });
    }

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String errorMessage);
    }
}


