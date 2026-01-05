// services/FirebaseTokenService.java
package com.petconnect.services;

import com.google.firebase.messaging.FirebaseMessaging;
import android.util.Log;

public class FirebaseTokenService {

    public void getFCMToken(TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Échec de récupération du token", task.getException());
                        callback.onError(task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM", "Token FCM: " + token);
                    callback.onSuccess(token);
                });
    }

    public interface TokenCallback {
        void onSuccess(String token);
        void onError(Exception e);
    }
}