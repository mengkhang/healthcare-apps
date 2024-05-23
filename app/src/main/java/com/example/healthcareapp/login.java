package com.example.healthcareapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {

    private TextInputEditText txtemail, txtps;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private FirestoreManager fStoreManager;
    private TextView registerAccTextView, forgetPStxt, txtLoginWithGoogle;
    private GoogleSignInClient client;
    private final LoadingDialog loadingDialog = new LoadingDialog(login.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtemail = findViewById(R.id.loginEmailInputEdit);
        txtps = findViewById(R.id.loginPasswordInputEdit);
        btnLogin = findViewById(R.id.loginBtn);
        mAuth = FirebaseAuth.getInstance();
        forgetPStxt = findViewById(R.id.forgetPStxt);
        registerAccTextView = findViewById(R.id.registerAcctxt);
        txtLoginWithGoogle = findViewById(R.id.txtLoginWithGoogle);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this,options);
        fStoreManager = new FirestoreManager();

        registerAccTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, signup_email.class);
                startActivity(intent);
            }
        });

        forgetPStxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgotps, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadingDialog.startLoadingDialog();
                        String userEmail = emailBox.getText().toString().trim();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            ToastUtils.showToast(login.this, "Enter your registered email id", Toast.LENGTH_SHORT);
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingDialog.dismissDialog();
                                if (task.isSuccessful()){
                                    ToastUtils.showToast(login.this, "Check your email inbox or spam", Toast.LENGTH_SHORT);
                                    dialog.dismiss();
                                } else {
                                    ToastUtils.showToast(login.this, "Unable to send, failed", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                });

                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, ps;
                email = String.valueOf(txtemail.getText()).trim();
                ps = String.valueOf(txtps.getText());
                if(TextUtils.isEmpty(email)){
                    ToastUtils.showToast(login.this, "Enter Email", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(ps)){
                    ToastUtils.showToast(login.this, "Enter Password", Toast.LENGTH_SHORT);
                    return;
                }
                loadingDialog.startLoadingDialog();
                if(email.equals("admin") && ps.equals("123")){
                    //it is admin login
                    //save admin login status to SharedPreference, so next time admin wont need to login again
                    adminLoginStatus.saveAdminLoginStatus(getApplicationContext(), true);
                    loadingDialog.dismissDialog();
                    startActivity(new Intent(getApplicationContext(), admin_profile.class));
                    finish();
                }else{
                    //it is user (doctor / patient) login
                    mAuth.signInWithEmailAndPassword(email, ps)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    loadingDialog.dismissDialog();
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        onStart();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        ToastUtils.showToast(login.this, "Authentication Failed.", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                }


            }
        });

        txtLoginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = client.getSignInIntent();
                loadingDialog.startLoadingDialog();
                startActivityForResult(i,1234); //this will active onActivityResult()...
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loadingDialog.dismissDialog();
                                if(task.isSuccessful()){
                                    onStart();
                                }else {
                                    ToastUtils.showToast(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT);
                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        isUserAvailable();
    }

    private void isUserAvailable() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Boolean isAdminLoggedIn = adminLoginStatus.isAdminLoggedIn(getApplicationContext());
        // Check if there is logged in user(doctor/patients) in the firebase auth (non-null)?
        if(currentUser != null){
            checkIsNewGoogleUser(currentUser);
        }// Check if there is logged in admin? (isAdminLoggedIn ==true)  # impossible to have current != null && isAdminLoggedIn == true at the same time, cuz only 1 user can login.
        else if(isAdminLoggedIn){
            //if yes, directly display admin screen.
            startActivity(new Intent(getApplicationContext(), admin_profile.class));
            finish();
        }
    }

    private void checkIsNewGoogleUser (FirebaseUser currentUser){
        //check if the signed in user is new google user anot
        FirebaseFirestore.getInstance().collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot user = task.getResult();
                    if (user.exists()) {
                        //not new google user..
                        // (if this user have uid in users collection already, that's mean he is email registered user / doctors / old google user, coz email account & old google account will have record in users collection already, but new sign in google user dont have.
                        checkRoleAndDisplayHomeScreen(currentUser);
                    } else {
                        //is new google user, prompt him to complete profile.
                        startActivity(new Intent(getApplicationContext(), signup_google.class));//TODO
                        finish();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }});
    }

    private void checkRoleAndDisplayHomeScreen(FirebaseUser currentUser){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getString("role").equals("doctor")){
                            startActivity(new Intent(login.this, doctor_profile.class));
                            finish();
                        }else if(documentSnapshot.getString("role").equals("patient")){
                            startActivity(new Intent(login.this, patient_profile.class));
                            finish();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("fail", "on Failure: " + e);
                    }
                });
    }
}

