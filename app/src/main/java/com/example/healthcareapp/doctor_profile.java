package com.example.healthcareapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthcareapp.fragment.fragment_order_main;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class doctor_profile extends AppCompatActivity {

    private Button btnLogout, btnDoctorUpcomingAppointment, btnProfile;
    private Uri uri;
    private ImageView doctorProfileImg;
    TextView txtWlcName;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        uri = null;
//        camBtn = findViewById(R.id.camBtn);
        doctorProfileImg = findViewById(R.id.doctorProfileImg);
        btnLogout = findViewById(R.id.btnLogout);
        btnDoctorUpcomingAppointment = findViewById(R.id.btnDoctorUpcomingAppointment);
        btnProfile = findViewById(R.id.btnProfile);
        txtWlcName = findViewById(R.id.txtWlcName);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        txtWlcName.setText(currentUser.getDisplayName());

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(doctor_profile.this, doctor_update_profile.class));
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        });

        btnDoctorUpcomingAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), view_appointment_list.class));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        txtWlcName.setText(currentUser.getDisplayName());
    }
}