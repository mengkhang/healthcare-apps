package com.example.healthcareapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class confirmationDialog {

    public interface ConfirmationListener {
        void onConfirm();
        void onCancel();
    }

    public static void show(Context context, String title, String message, final ConfirmationListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onConfirm();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                })
                .show();
    }
}