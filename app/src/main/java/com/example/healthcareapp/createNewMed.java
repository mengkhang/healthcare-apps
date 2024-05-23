


package com.example.healthcareapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.Firestore.FirestoreCallback;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class createNewMed extends AppCompatActivity {

    Button selectImagebtn, btnCreate;
    TextInputEditText medNametxt, medRinggittxt, medSentxt, medDestxt, medSuittxt, medAvoidtxt;
    String medName, medRinggit, medSen, medDes, medSuit, medAvoid;
    ImageView firebaseimage;
    Uri imageUri;
    FireStorageManager fStorageManager;
    FirestoreManager fStoreManager;
    final LoadingDialog loadingDialog = new LoadingDialog(createNewMed.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_med);

        fStorageManager = new FireStorageManager();
        fStoreManager = new FirestoreManager();
        btnCreate = findViewById(R.id.btnCreate);
        selectImagebtn = findViewById(R.id.selectImagebtn);
        medNametxt= findViewById(R.id.medNameInputEdit);
        medRinggittxt= findViewById(R.id.ringgitInputEdit);
        medSentxt= findViewById(R.id.senInputEdit);
        medDestxt= findViewById(R.id.desInputEdit);
        medSuittxt= findViewById(R.id.suitInputEdit);
        medAvoidtxt= findViewById(R.id.avoidInputEdit);
        firebaseimage = findViewById(R.id.firebaseimage);
        firebaseimage.setImageURI(null);


        // restrict input to letters and spaces only in textfield  "medNameInputEdit"
        medNametxt.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // Define the allowed characters using a regular expression
                String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
                for (int i = start; i < end; i++) {
                    if (!allowedChars.contains(String.valueOf(source.charAt(i)))) {
                        return ""; // Remove the disallowed character
                    }
                }
                return null; // Accept the input
            }
        }});

        //restrict 2 number for price - sen only
        InputFilter lengthFilter = new InputFilter.LengthFilter(2);
        medSentxt.setFilters(new InputFilter[]{lengthFilter});

        selectImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medName = String.valueOf(medNametxt.getText()).trim();
                medRinggit = String.valueOf(medRinggittxt.getText());
                medSen = String.valueOf(medSentxt.getText());
                medDes = String.valueOf(medDestxt.getText()).trim();
                medSuit = String.valueOf(medSuittxt.getText()).trim();
                medAvoid = String.valueOf(medAvoidtxt.getText()).trim();

                if(TextUtils.isEmpty(medName)){
                    ToastUtils.showToast(createNewMed.this, "Enter Medicine Name", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(medDes)){
                    ToastUtils.showToast(createNewMed.this, "Enter description", Toast.LENGTH_SHORT);
                    return;
                }

                if(TextUtils.isEmpty(medSuit)){
                    ToastUtils.showToast(createNewMed.this, "Enter suitable for...", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(medAvoid)){
                    ToastUtils.showToast(createNewMed.this, "Enter avoid it when...", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(medRinggit)){
                    ToastUtils.showToast(createNewMed.this, "Enter price Ringgit", Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(medSen)){
                    ToastUtils.showToast(createNewMed.this, "Enter price Sen", Toast.LENGTH_SHORT);
                    return;
                }
                if(imageUri == null){
                    ToastUtils.showToast(createNewMed.this, "Select image for medicine", Toast.LENGTH_SHORT);
                    return;
                }
                createMed();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    public void createMed(){
        //1. check if the doc is exist in database.
        fStoreManager.getADoc("medicine", medName, new FirestoreCallback() {
            @Override
            public void onFirestoreCallback_exists(Map<String, Object> data) {
                //1.1  yes it is exist, dont create it
                ToastUtils.showToast(createNewMed.this, "medicine name is exist, please select a new name", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFirestoreCallback_notexists() {
                //1.2 no it is not exist, create it
                Map<String, Object> data = new HashMap<>();
                data.put("avoid", medAvoid);
                data.put("des", medDes);
                data.put("ringgit", medRinggit);
                data.put("sen", medSen);
                data.put("suit", medSuit);
                //upload details to firestore
                fStoreManager.createDoc("medicine", medName, data, new onUpdateListener() {
                    @Override
                    public void onUpdateListener_success() {
                        //upload image to firebase storage
                        uploadImage();
                    }

                    @Override
                    public void onUpdateListener_fail(Exception e) {
                        Log.d("exception_gg", e.toString());
                        ToastUtils.showToast(createNewMed.this, "fail to create document in the medicine collection:", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void uploadImage() {
        loadingDialog.startLoadingDialog();
        String fileName = medName; //example: antibiotic, Anascorp_Centruroides
        Log.d("fileName", fileName);
        fStorageManager.saveImage(createNewMed.this, "medicine", fileName, imageUri, new onUpdateListener() {
            @Override
            public void onUpdateListener_success() {
                loadingDialog.dismissDialog();
                finish();
            }

            @Override
            public void onUpdateListener_fail(Exception e) {
                loadingDialog.dismissDialog();
                Toast.makeText(createNewMed.this,"Failed to Upload",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            Log.d("imageUri", imageUri+""); //com.android.providers.media.documents/document/image%3A1000000302
            firebaseimage.setImageURI(imageUri);
        }
    }
}