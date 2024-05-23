package com.example.healthcareapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup_email extends AppCompatActivity {
    private TextInputEditText txtemail, txtps, txtconfPs, txtName, txtTp, txtAd;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirestoreManager fStoreManager;
    private TextView loginNav;
    String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        final LoadingDialog loadingDialog = new LoadingDialog(signup_email.this);
        mAuth = FirebaseAuth.getInstance();
        txtemail = findViewById(R.id.emailInputEdit);
        txtps = findViewById(R.id.passwordInputEdit);
        txtconfPs = findViewById(R.id.confirmPasswordInputEdit);
        txtName = findViewById(R.id.nameInputEdit);
        txtTp = findViewById(R.id.tpInputEdit);
        txtAd = findViewById(R.id.adInputEdit);
        btnSignUp = findViewById(R.id.btnSignUp);
        loginNav = findViewById(R.id.textLoginNav);
        fStore = FirebaseFirestore.getInstance();
        fStoreManager = new FirestoreManager();

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email, ps, confPs, name, tp, ad;
                email = String.valueOf(txtemail.getText()).trim();
                ps = String.valueOf(txtps.getText());
                confPs = String.valueOf(txtconfPs.getText());
                name = String.valueOf(txtName.getText()).trim();
                tp = String.valueOf(txtTp.getText()).trim();
                ad = String.valueOf(txtAd.getText()).trim();

                if(TextUtils.isEmpty(name)){
                    ToastUtils.showToast(signup_email.this, "Enter Name", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    ToastUtils.showToast(signup_email.this, "Enter Email", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(tp)){
                    ToastUtils.showToast(signup_email.this, "Enter Telephone", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(ad)){
                    ToastUtils.showToast(signup_email.this, "Enter Address", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(ps)){
                    ToastUtils.showToast(signup_email.this, "Enter Password", Toast.LENGTH_SHORT);
                    return;
                }

                if(TextUtils.isEmpty(confPs)){
                    ToastUtils.showToast(signup_email.this, "Enter Confirm Password", Toast.LENGTH_SHORT);
                    return;
                }
                if(!ps.equals(confPs)){
                    ToastUtils.showToast(signup_email.this, "Password should be match", Toast.LENGTH_SHORT);
                    return;
                }
                loadingDialog.startLoadingDialog();
                mAuth.createUserWithEmailAndPassword(email, ps)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //set user display name in firebase auth
                                    fStoreManager.updateUser_displayName(name);
                                    userUID = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fStore.collection("patients").document(userUID);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name",name );
                                    user.put("phone",tp );
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
                                                    Intent intent = new Intent(signup_email.this, patient_profile.class);
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
                                } else {
                                    // If sign in fails, display a message to the user.
                                    ToastUtils.showToast(signup_email.this, "Authentication failed.", Toast.LENGTH_SHORT);
                                }
                            }
                        });
            }
        });

        loginNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup_email.this, login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}