package com.example.healthcareapp;

import android.content.Context;
import android.content.SharedPreferences;

public class adminLoginStatus {
    private static final String ADMIN_PREFS_NAME = "AdminPrefs";

    public static void saveAdminLoginStatus(Context context, boolean isLoggedIn) {
        SharedPreferences preferences = context.getSharedPreferences(ADMIN_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isAdminLoggedIn", isLoggedIn);
        editor.apply();
    }

    public static boolean isAdminLoggedIn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ADMIN_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean("isAdminLoggedIn", false); // Default to false if the key is not present
    }
}
