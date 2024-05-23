package com.example.healthcareapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class user_profile extends AppCompatActivity {

    Button btnUpcomingAppointment;
    FloatingActionButton cameraBtn;
    ImageView userProfileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        cameraBtn = findViewById(R.id.cameraBtn);
        userProfileImg = findViewById(R.id.userProfileImg);
        btnUpcomingAppointment = findViewById(R.id.btnUpcomingAppointment);
        btnUpcomingAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), view_appointment_info.class));

            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(user_profile.this)
                        .crop(1f, 1f)	    //auto crop to 1:1 ratio after user select photo.
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        userProfileImg.setImageURI(uri);
    }
}