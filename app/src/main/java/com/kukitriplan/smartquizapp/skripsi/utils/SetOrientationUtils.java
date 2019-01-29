package com.kukitriplan.smartquizapp.skripsi.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

public final class SetOrientationUtils {

    public static void SetFull(@NonNull Activity activity) {
        activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void SetTitle(@NonNull Activity activity) {
        activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
