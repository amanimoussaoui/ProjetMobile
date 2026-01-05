package com.Projet.forum;

import android.app.PendingIntent;
import android.content.Intent;import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = "";
        String body = "";

        // Case 1: Standard Notification (from Firebase Console)
        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }
        // Case 2: Data Message (User-to-User interaction like Likes/Comments)
        else if (message.getData().size() > 0) {
            title = message.getData().get("title");
            body = message.getData().get("body");
        }

        if (title != null && !title.isEmpty()) {
            sendVisualNotification(title, body);
        }
    }

    private void sendVisualNotification(String title, String body) {
        Intent intent = new Intent(this, ForumListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "forum_notifications")
                .setSmallIcon(R.drawable.ic_home)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Permission check for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "New Token generated: " + token);
        updateTokenInFirestore(token);
    }

    private void updateTokenInFirestore(String token) {
        User currentUser = FirebaseStorageHelper.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId() != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.getId())
                    .update("fcmToken", token)
                    .addOnSuccessListener(aVoid -> Log.d("FCM", "Firestore token updated."));
        }
    }
}
