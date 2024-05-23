package com.example.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.healthcareapp.fragment.fragment_order_main;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class admin_profile extends AppCompatActivity {

    AppCompatButton btnRegisterNewDoc, btnLogout, btnAddNewMed, btnCustOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        btnRegisterNewDoc = findViewById(R.id.btnRegisterNewDoc);
        btnLogout = findViewById(R.id.btnLogout);
        btnAddNewMed = findViewById(R.id.btnAddNewMed);
        btnCustOrder = findViewById(R.id.btnCustOrder);
        btnRegisterNewDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(admin_profile.this, register_new_doc.class));
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser  = FirebaseAuth.getInstance().getCurrentUser();
                //if admin have registered doctor, currentUser will be the registered doctor
                if(currentUser != null){
                    FirebaseAuth.getInstance().signOut(); //logout the register doctor that just registers, else in login page it will automatically login the registered doctor
                }


                adminLoginStatus.saveAdminLoginStatus(admin_profile.this, false);
                startActivity(new Intent(admin_profile.this, login.class));
                finish();
            }
        });
        btnAddNewMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(admin_profile.this, createNewMed.class));
            }
        });
        btnCustOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_profile.this, fragment_order_main.class);
                intent.putExtra("role", "admin");
                startActivity(intent);
            }
        });

    }
}