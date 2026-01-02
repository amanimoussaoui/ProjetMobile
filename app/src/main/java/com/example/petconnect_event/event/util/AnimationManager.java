package com.example.petconnect_event.event.util;

import android.app.Activity;
import com.example.petconnect_event.R;

public class AnimationManager {
    
    public static void startActivityWithAnimation(Activity currentActivity, Activity targetActivity) {
        currentActivity.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        );
    }

    public static void finishActivityWithAnimation(Activity activity) {
        activity.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }

    public static void startActivityWithFadeAnimation(Activity currentActivity, Activity targetActivity) {
        currentActivity.overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        );
    }

    public static void startActivityWithScaleAnimation(Activity currentActivity, Activity targetActivity) {
        currentActivity.overridePendingTransition(
            R.anim.scale_in,
            R.anim.fade_out
        );
    }

    public static void startActivityWithBounceAnimation(Activity currentActivity, Activity targetActivity) {
        currentActivity.overridePendingTransition(
            R.anim.bounce_in,
            R.anim.fade_out
        );
    }
}
