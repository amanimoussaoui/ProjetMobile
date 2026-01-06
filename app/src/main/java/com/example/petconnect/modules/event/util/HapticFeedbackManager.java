package com.example.petconnect.modules.event.util;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.content.Context;

public class HapticFeedbackManager {
    
    public static void vibrate(View view, int duration) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    public static void vibrateLong(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
        }
    }

    public static void vibrateClick(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view.performHapticFeedback(android.view.HapticFeedbackConstants.CONTEXT_CLICK);
        }
    }

    public static void vibrateSuccess(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM);
        }
    }

    public static void vibrateError(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            view.performHapticFeedback(android.view.HapticFeedbackConstants.REJECT);
        }
    }
}
