package com.example.healthcareapp.utils;

import android.content.Context;
import android.widget.Toast;

//this class is used to shown Toast. (the current toast message will dissapear when another new toast message pop up)
public class ToastUtils {

    private static Toast currentToast;

    public static void showToast(Context context, String message, int duration) {
        // Cancel the current toast if it is visible
        cancelCurrentToast();

        // Create a new toast
        currentToast = Toast.makeText(context, message, duration);

        // Show the toast
        currentToast.show();
    }

    private static void cancelCurrentToast() {
        // Cancel the current toast if it is not null
        if (currentToast != null) {
            currentToast.cancel();
            currentToast = null;
        }
    }
}
