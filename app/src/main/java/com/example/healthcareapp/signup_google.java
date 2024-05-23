package com.example.healthcareapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup_google extends AppCompatActivity {
    private TextInputEditText txtemail, txtps, txtconfPs, txtName, txtTp, txtAd;
    private TextInputLayout txtpsLayout, txtConfPsLayout;
    private Button btnUpdate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private TextView cancel, title;
    private FirebaseUser currentUser;
    private FirestoreManager fStoreManager;
    String userUID;
    final LoadingDialog loadingDialog = new LoadingDialog(signup_google.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        txtemail = findViewById(R.id.emailInputEdit);
        txtps = findViewById(R.id.passwordInputEdit);
        txtconfPs = findViewById(R.id.confirmPasswordInputEdit);
        txtName = findViewById(R.id.nameInputEdit);
        txtTp = findViewById(R.id.tpInputEdit);
        btnUpdate = findViewById(R.id.btnSignUp);
        cancel = findViewById(R.id.textLoginNav);
        txtpsLayout = findViewById(R.id.passwordInputLayout);
        txtConfPsLayout = findViewById(R.id.confirmPasswordInputLayout);
        txtAd = findViewById(R.id.adInputEdit);
        title = findViewById(R.id.titlee);
        currentUser = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        fStoreManager = new FirestoreManager();

        title.setText("Few More Step to Complete Registration");
        cancel.setText("Cancel");
        btnUpdate.setText("Update");
        //change constraint layout top to bottom of telephone.
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) btnUpdate.getLayoutParams();
        layoutParams.topToBottom = R.id.adInputLayout;
        btnUpdate.setLayoutParams(layoutParams);
        //-----------------------------------
        txtemail.setText(currentUser.getEmail());
        txtemail.setEnabled(false);
        txtpsLayout.setVisibility(View.INVISIBLE);
        txtConfPsLayout.setVisibility(View.INVISIBLE);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,  tp, ad, email;
                name = String.valueOf(txtName.getText()).trim();
                tp = String.valueOf(txtTp.getText()).trim();
                ad = String.valueOf(txtAd.getText()).trim();
                email = String.valueOf(txtemail.getText()).trim();
                if(TextUtils.isEmpty(name)){
                    ToastUtils.showToast(signup_google.this, "Enter Name", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(tp)){
                    ToastUtils.showToast(signup_google.this, "Enter Telephone", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(ad)){
                    ToastUtils.showToast(signup_google.this, "Enter Address", Toast.LENGTH_SHORT);
                    return;
                }
                saveProfileToFirestore(name, tp, ad, email);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel(); //this will delete user from authentication firebase, and call login.java
            }
        });
    }

    private void saveProfileToFirestore(String name, String tp, String ad, String email) {
        loadingDialog.startLoadingDialog();
        //set display name in firebase auth
        fStoreManager.updateUser_displayName(String.valueOf(txtName.getText()).trim());
        //Save profile data in firestore.
        userUID = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("patients").document(userUID);
        Map<String, Object> user = new HashMap<>();
        user.put("name", name );
        user.put("phone", tp);
        user.put("email",email );
        user.put("appointmentID_patient", "");
        user.put("address", ad);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                user.clear();
                user.put("role", "patient");
                fStoreManager.createDoc("users", userUID, user, new onUpdateListener() {
                    @Override
                    public void onUpdateListener_success() {
                        loadingDialog.dismissDialog();
                        Intent intent = new Intent(signup_google.this, patient_profile.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onUpdateListener_fail(Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

    }

    private void cancel(){
        loadingDialog.startLoadingDialog();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), login.class));
        finish();
    }
}