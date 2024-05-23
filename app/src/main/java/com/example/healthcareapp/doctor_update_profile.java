package com.example.healthcareapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class doctor_update_profile extends AppCompatActivity {

    TextInputEditText nameInputEdit, emailInputEdit, tpInputEdit, specInputEdit, accmphInputEdit;
    Button btnChgPassword, btnUpdateDoctor;
    FloatingActionButton camBtn;
    FirestoreManager fStoreManager;
    String currentUserUID;
    ImageView doctorProfileImg;
    Uri doctorProfileImgUri;
    FireStorageManager fStorageManager;
    final LoadingDialog loadingDialog = new LoadingDialog(doctor_update_profile.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_update_profile);
        nameInputEdit=findViewById(R.id.nameInputEdit);
        emailInputEdit=findViewById(R.id.emailInputEdit);
        tpInputEdit=findViewById(R.id.tpInputEdit);
        specInputEdit=findViewById(R.id.specInputEdit);
        accmphInputEdit=findViewById(R.id.accmphInputEdit);
        btnChgPassword=findViewById(R.id.btnChgPassword);
        btnUpdateDoctor=findViewById(R.id.btnUpdateDoctor);
        camBtn=findViewById(R.id.camBtn);
        doctorProfileImg = findViewById(R.id.doctorProfileImg);
        fStoreManager= new FirestoreManager();
        currentUserUID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStorageManager = new FireStorageManager();
        doctorProfileImgUri = null;
        init();
    }

    public void init(){
        emailInputEdit.setEnabled(false);
        fStorageManager.setImageOnImageView(doctor_update_profile.this, "doctor", currentUserUID, doctorProfileImg); //set Image
        fStoreManager.getADoc("doctors", currentUserUID, new FirestoreCallback() {
            @Override
            public void onFirestoreCallback_exists(Map<String, Object> data) {
                nameInputEdit.setText(data.get("name").toString());
                emailInputEdit.setText(data.get("email").toString());
                tpInputEdit.setText(data.get("phone").toString());
                specInputEdit.setText(data.get("specialties").toString());
                accmphInputEdit.setText(data.get("accomplishment").toString());
            }

            @Override
            public void onFirestoreCallback_notexists() {

            }
        });

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open image picker to let user select image. the selected img will be handle by onActivityResult() within this class.
                ImagePicker.with(doctor_update_profile.this)
                        .crop(1f, 1f)	    //auto crop to 1:1 ratio after user select photo.
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
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
                        ToastUtils.showToast(doctor_update_profile.this, "Google Sign In User cannot change password", Toast.LENGTH_SHORT);
                    } else if (providerId.equals(EmailAuthProvider.PROVIDER_ID)) {
                        //2. User account is signed in with Email/Password
                        showChangePsDialog();
                    }
                }
            }
        });

        btnUpdateDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDoctorProfile();
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
            ToastUtils.showToast(doctor_update_profile.this, "Enter All field", Toast.LENGTH_SHORT);
        } else if (!newPs.equals(confPs)) {
            loadingDialog.dismissDialog();
            ToastUtils.showToast(doctor_update_profile.this, "Confirm password must be same as new password", Toast.LENGTH_SHORT);
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
                                            ToastUtils.showToast(doctor_update_profile.this, "Password updated successfully", Toast.LENGTH_SHORT);
                                            finish();
                                        } else {
                                            // the update fails, display a message to the user.
                                            loadingDialog.dismissDialog();
                                            Log.e("doctor_update_profile.java ", "Error updating password: " + task.getException().getMessage());
                                            ToastUtils.showToast(doctor_update_profile.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT);
                                        }
                                    }
                                });
                            } else {
                                loadingDialog.dismissDialog();
                                // Reauthentication failed, the entered password is incorrect
                                ToastUtils.showToast(doctor_update_profile.this, "Old password incorrect", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }

    private void updateDoctorProfile() {
        loadingDialog.startLoadingDialog();
        //1. save information to firestore
        String accomplishment, email, name, phone, specs;
        accomplishment = accmphInputEdit.getText().toString();
        email = emailInputEdit.getText().toString();
        name = nameInputEdit.getText().toString();
        phone = tpInputEdit.getText().toString();
        specs = specInputEdit.getText().toString();
        if(TextUtils.isEmpty(accomplishment) || TextUtils.isEmpty(email) || TextUtils.isEmpty(name)|| TextUtils.isEmpty(phone)|| TextUtils.isEmpty(specs)){
            ToastUtils.showToast(doctor_update_profile.this, "Input All Field", Toast.LENGTH_SHORT);
        }else {
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("accomplishment", accomplishment);
            updateMap.put("email", email);
            updateMap.put("name", name);
            updateMap.put("phone", phone);
            updateMap.put("specialties", specs);
            //update name to firebaseAuth
            fStoreManager.updateUser_displayName(name);
            //update modification to fireStore
            fStoreManager.updateField("doctors", currentUserUID, updateMap, new onUpdateListener() {
                @Override
                public void onUpdateListener_success() {
                    //2. Save image to firebase storage here, only if user have uploaded image (!=null)
                    if (doctorProfileImgUri == null) {
                        loadingDialog.dismissDialog();
                        ToastUtils.showToast(doctor_update_profile.this, "Updated Successfully", Toast.LENGTH_SHORT);
                    } else if (doctorProfileImgUri != null) {
                        fStorageManager.saveImage(doctor_update_profile.this, "doctor", currentUserUID, doctorProfileImgUri, new onUpdateListener() {
                            @Override
                            public void onUpdateListener_success() {
                                loadingDialog.dismissDialog();
                                ToastUtils.showToast(doctor_update_profile.this, "Updated Successfully", Toast.LENGTH_SHORT);
                                finish();
                            }

                            @Override
                            public void onUpdateListener_fail(Exception e) {
                                loadingDialog.dismissDialog();
                                ToastUtils.showToast(doctor_update_profile.this, "Failed upload photo: " + e.getMessage(), Toast.LENGTH_SHORT);
                            }
                        });
                    }
                }

                @Override
                public void onUpdateListener_fail(Exception e) {
                    loadingDialog.dismissDialog();
                    ToastUtils.showToast(doctor_update_profile.this, "Failed Upload information: " + e.getMessage(), Toast.LENGTH_SHORT);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this function handle the selected image by user.
        super.onActivityResult(requestCode, resultCode, data);
        //data.getData() is the selected image Uri... it'll set to the this class properties "doctorProfileImgUri"
        doctorProfileImgUri = data.getData();
        //use the uri set it to imageview "doctorProfileImg", now doctorProfileImg have the uri to the selected image,
        doctorProfileImg.setImageURI(doctorProfileImgUri);
    }
}