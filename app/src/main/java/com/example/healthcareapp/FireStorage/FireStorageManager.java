package com.example.healthcareapp.FireStorage;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.R;
import com.example.healthcareapp.createNewMed;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.internal.StorageReferenceUri;

import java.io.File;
import java.io.IOException;

public class FireStorageManager {

    StorageReference gsReference;
    public FireStorageManager() {
        gsReference = FirebaseStorage.getInstance().getReference();
    }

    public void saveImage(Context context, String pathName, String fileName, Uri imageUri, onUpdateListener onUpdateListener){
        if(gsReference==null){
            throw new IllegalStateException("FireStorageManager is not properly initialized, u havent create constructor.");
        }
        FirebaseStorage.getInstance().getReference(pathName+"/"+fileName+".jpg").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        onUpdateListener.onUpdateListener_success();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed to Upload",Toast.LENGTH_SHORT).show();
                        onUpdateListener.onUpdateListener_fail(e);
                    }
                });
    }
    public void setImageOnImageView(Context context, String directory, String imageFileName, ImageView imageView){
        if(gsReference==null){
            throw new IllegalStateException("FireStorageManager is not properly initialized, u havent create constructor.");
        }
        //STEP TO GET URL OF IMAGE IN FIRESTORAGE
        // 1. create a reference to firebase storage.
//        gsReference = FirebaseStorage.getInstance().getReference();
        // 2. navigate to image path to get url
        gsReference.child(directory + "/" + imageFileName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // 3. Got the download URL for "medicine/Kill.jpg", use Glide library to it put on imageView.
                Glide.with(context)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_error) //.placeholder() are drawable that are shown while the request is in progress.
                        .into(imageView); //put the image into this imageView variable.
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Image Fail: "+exception.getMessage());
                if (exception instanceof StorageException && ((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    // Doctor havent upload any image
                    //Do nothing because the list will show default profile pic -> ic_account_24dp.xml
                } else {
                    // Fail to get image due to other reason.
                    ToastUtils.showToast(context, "Failed to get image", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void viewFullImage_fromFirebase(Context context, String directory, String imageFileName) {
        if(gsReference==null){
            throw new IllegalStateException("FireStorageManager is not properly initialized, u havent create constructor.");
        }
        FirebaseStorage.getInstance().getReference().child(directory + "/" + imageFileName+ ".jpg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setDataAndType(uri, "image/*");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
    }

}
