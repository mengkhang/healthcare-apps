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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class register_new_doc extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirestoreManager fStoreManager;
    private TextInputEditText txtemail, txtps, txtconfPs, txtName, txtTp, txtSpec, txtAccmph;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_doc);

        final LoadingDialog loadingDialog = new LoadingDialog(register_new_doc.this);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        btnRegister = findViewById(R.id.btnRegister);
        txtemail = findViewById(R.id.emailInputEdit);
        txtps = findViewById(R.id.passwordInputEdit);
        txtconfPs = findViewById(R.id.confirmPasswordInputEdit);
        txtName = findViewById(R.id.nameInputEdit);
        txtTp = findViewById(R.id.tpInputEditt);
        txtSpec = findViewById(R.id.specInputEdit);
        txtAccmph = findViewById(R.id.accmphInputEdit);
        fStoreManager = new FirestoreManager();

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String email, ps, confPs, name, tp, accmph, spec;
                email = String.valueOf(txtemail.getText()).trim();
                ps = String.valueOf(txtps.getText());
                confPs = String.valueOf(txtconfPs.getText());
                name = String.valueOf(txtName.getText()).trim();
                tp = String.valueOf(txtTp.getText()).trim();
                accmph = String.valueOf(txtAccmph.getText()).trim();
                spec = String.valueOf(txtSpec.getText()).trim();
                if(TextUtils.isEmpty(name)){
                    ToastUtils.showToast(register_new_doc.this, "Enter Name", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    ToastUtils.showToast(register_new_doc.this, "Enter Email", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(tp)){
                    ToastUtils.showToast(register_new_doc.this, "Enter Telephone", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(spec)){
                    ToastUtils.showToast(register_new_doc.this, "Enter Specialties", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(confPs)){
                    ToastUtils.showToast(register_new_doc.this, "Enter Confirm Password", Toast.LENGTH_SHORT);
                    return;
                }
                if(!ps.equals(confPs)){
                    ToastUtils.showToast(register_new_doc.this, "Password should be match", Toast.LENGTH_SHORT);
                    return;
                }
                loadingDialog.startLoadingDialog();
                mAuth.createUserWithEmailAndPassword(email, ps)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //set display name
                                    fStoreManager.updateUser_displayName(name);
                                    //Save profile data in firestore.
                                    DocumentReference documentReference = fStore.collection("doctors").document(mAuth.getCurrentUser().getUid());
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name",name );
                                    user.put("phone",tp );
                                    user.put("email",email );
                                    user.put("specialties",spec );
                                    user.put("accomplishment",accmph );
                                    user.put("unavailable_dateTime", new ArrayList<>());
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            user.clear();
                                            user.put("role", "doctor");
                                            fStoreManager.createDoc("users", mAuth.getCurrentUser().getUid(), user, new onUpdateListener() {
                                                @Override
                                                public void onUpdateListener_success() {
                                                    loadingDialog.dismissDialog();
                                                    Intent intent = new Intent(register_new_doc.this, admin_profile.class);
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
                                    ToastUtils.showToast(register_new_doc.this, "Register failed.", Toast.LENGTH_SHORT);
                                }
                            }
                        });
            }
        });
    }
}