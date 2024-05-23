package com.example.healthcareapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.Firestore.FirestoreCallback;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class patient_update_profile extends AppCompatActivity {

    FireStorageManager fStorageManager;
    FirestoreManager fStoreManager;
    FirebaseUser currentUser;
    TextInputEditText nameInputEdit, emailInputEdit, tpInputEdit,  adInputEdit;
    Button btnChgPassword, btnUpdatePatient;
    final LoadingDialog loadingDialog = new LoadingDialog(patient_update_profile.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_update_profile);

        fStorageManager = new FireStorageManager();
        fStoreManager = new FirestoreManager();
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        nameInputEdit=findViewById(R.id.nameInputEdit);
        emailInputEdit=findViewById(R.id.emailInputEdit);
        tpInputEdit=findViewById(R.id.tpInputEdit);
        adInputEdit=findViewById(R.id.adInputEdit);
        btnChgPassword=findViewById(R.id.btnChgPassword);
        btnUpdatePatient=findViewById(R.id.btnUpdatePatient);
        init();
    }

    public void init(){
        emailInputEdit.setEnabled(false);
        fStoreManager.getADoc("patients", currentUser.getUid(), new FirestoreCallback() {
            @Override
            public void onFirestoreCallback_exists(Map<String, Object> data) {
                nameInputEdit.setText(data.get("name").toString());
                emailInputEdit.setText(data.get("email").toString());
                tpInputEdit.setText(data.get("phone").toString());
                adInputEdit.setText(data.get("address").toString());
            }

            @Override
            public void onFirestoreCallback_notexists() {
                ToastUtils.showToast(patient_update_profile.this, "User Profile not exists..", Toast.LENGTH_SHORT);
            }
        });

        btnChgPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //below code check if user sign in with google or email.
                List<? extends UserInfo> providers = FirebaseAuth.getInstance().getCurrentUser().getProviderData();
                for (UserInfo userInfo : providers) {
                    String providerId = userInfo.getProviderId();
                    // Check the provider ID
                    if (providerId.equals(GoogleAuthProvider.PROVIDER_ID)) {
                        //1. User account is signed in with Google
                        ToastUtils.showToast(patient_update_profile.this, "Google Sign In User cannot change password", Toast.LENGTH_SHORT);
                    } else if (providerId.equals(EmailAuthProvider.PROVIDER_ID)) {
                        //2. User account is signed in with Email/Password
                        showChangePsDialog();
                    }
                }
            }
        });

        btnUpdatePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePatientProfile();
            }
        });
    }

    private void showChangePsDialog() {
        AlertDialog.Builder chgPsBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_chgps, null);
        chgPsBuilder.setView(dialogView);
        final TextInputEditText oldPs = dialogView.findViewById(R.id.oldpsInputEdit);
        final TextInputEditText newPs = dialogView.findViewById(R.id.newpsInputEdit);
        final TextInputEditText confPs = dialogView.findViewById(R.id.confpsInputEdit);
        final Button btnChangePs = dialogView.findViewById(R.id.btnChangePs);

        btnChangePs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                AuthenticatePs_updatePs(oldPs.getText().toString(), newPs.getText().toString(), confPs.getText().toString());
            }
        });

        chgPsBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog trackingNumberDialog = chgPsBuilder.create();
        trackingNumberDialog.show();
    }

    public void AuthenticatePs_updatePs(String OldPs, String newPs, String confPs){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (TextUtils.isEmpty(OldPs) || TextUtils.isEmpty(newPs) || TextUtils.isEmpty(confPs)) {
            loadingDialog.dismissDialog();
            ToastUtils.showToast(patient_update_profile.this, "Enter All field", Toast.LENGTH_SHORT);
        } else if (!newPs.equals(confPs)) {
            loadingDialog.dismissDialog();
            ToastUtils.showToast(patient_update_profile.this, "Confirm password must be same as new password", Toast.LENGTH_SHORT);
        } else {
            //below logic is to authenticate password by checking if the old password is same as the one store in firebase auth
            AuthCredential credential = EmailAuthProvider.getCredential(emailInputEdit.getText().toString(), OldPs);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Reauthentication successful, the entered password is correct, UPDATE PASSWORD BELOW:
                                user.updatePassword(newPs).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Password updated successfully
                                            loadingDialog.dismissDialog();
                                            ToastUtils.showToast(patient_update_profile.this, "Password updated successfully", Toast.LENGTH_SHORT);
                                            finish();
                                        } else {
                                            // the update fails, display a message to the user.
                                            loadingDialog.dismissDialog();
                                            Log.e("patient_update_profile.java ", "Error updating password: " + task.getException().getMessage());
                                            ToastUtils.showToast(patient_update_profile.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT);
                                        }
                                    }
                                });
                            } else {
                                loadingDialog.dismissDialog();
                                // Reauthentication failed, the entered password is incorrect
                                ToastUtils.showToast(patient_update_profile.this, "Old password incorrect", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }

    private void updatePatientProfile() {
        loadingDialog.startLoadingDialog();
        //1. save information to firestore
        String email, name, phone, ad;
        email = emailInputEdit.getText().toString();
        name = nameInputEdit.getText().toString();
        phone = tpInputEdit.getText().toString();
        ad = adInputEdit.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(name)|| TextUtils.isEmpty(phone)|| TextUtils.isEmpty(ad)){
            ToastUtils.showToast(patient_update_profile.this, "Input All Field", Toast.LENGTH_SHORT);
        }else{
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("email", email);
        updateMap.put("name", name);
        updateMap.put("phone", phone);
        updateMap.put("address", ad);
        //update name to firebaseAuth
        fStoreManager.updateUser_displayName(name);
        //update modification to fireStore
        fStoreManager.updateField("patients", currentUser.getUid(), updateMap, new onUpdateListener() {
            @Override
            public void onUpdateListener_success() {
                loadingDialog.dismissDialog();
                ToastUtils.showToast(patient_update_profile.this, "Information Updated Successfully", Toast.LENGTH_SHORT);
            }
            @Override
            public void onUpdateListener_fail(Exception e) {
                loadingDialog.dismissDialog();
                ToastUtils.showToast(patient_update_profile.this, "Failed Upload information: "+e.getMessage(), Toast.LENGTH_SHORT);
            }
        });}
    }
}