package com.example.healthcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareapp.fragment.fragment_order_main;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class patient_profile extends AppCompatActivity {

    Button btnLogout, btnProfile, btnBookAppointment, btnUpcomeAppoint, btnNearbyHosp, btnMedShop, btnOrders;
    TextView txtWlcName;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    String name;
    final LoadingDialog loadingDialog = new LoadingDialog(patient_profile.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        txtWlcName = findViewById(R.id.txtWlcName);
        btnLogout = findViewById(R.id.btnLogout);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        btnUpcomeAppoint = findViewById(R.id.btnUpcomeAppoint);
        btnProfile = findViewById(R.id.btnProfile);
        btnNearbyHosp = findViewById(R.id.btnNearbyHosp);
        btnMedShop = findViewById(R.id.btnMedShop);
        btnOrders = findViewById(R.id.btnOrders);

        if(user ==null){//if user is not logged in
            Intent intent = new Intent(patient_profile.this, login.class); //prompt them to loginn
            startActivity(intent);
            finish();
        }else { //if user is logged in, shown this page, and set name.
//            txtWlcName.setText(user.getDisplayName());
            setName();
        }

        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patient_profile.this, fragment_order_main.class);
                intent.putExtra("role", "patient");
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(patient_profile.this, login.class);
                startActivity(intent);
                finish();
            }
        });
        btnMedShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(patient_profile.this, medicine_list.class);
                startActivity(intent);
            }
        });

        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fStore.collection("patients").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getString("appointmentID_patient").equals(""))  //If patient have no book any appointment, then show this screen
                            startActivity(new Intent(patient_profile.this, doctor_list.class));
                        else
                            ToastUtils.showToast(patient_profile.this, "You can only have 1 appointment at a time", Toast.LENGTH_LONG);
                    }
                })  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("fail", " failed on checking appointmentID_patient field : " + e);
                    }
                });

            }
        });
        btnUpcomeAppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(patient_profile.this, view_appointment_info.class));
            }
        });
        btnNearbyHosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(patient_profile.this, MapsActivity.class));
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(patient_profile.this, patient_update_profile.class));
            }
        });
    }

    public void setName(){
        DocumentReference docRef = fStore.collection("patients").document(user.getUid());
        Log.d("uid", ""+user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name = documentSnapshot.getString("name");
                        txtWlcName.setText(name);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("fail", "on Failure: " + e);
                    }
                });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
//        txtWlcName.setText(user.getDisplayName());
        setName();
    }
}