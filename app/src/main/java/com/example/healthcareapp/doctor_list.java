package com.example.healthcareapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import com.example.healthcareapp.adapter.doctorListAdapter;
import com.example.healthcareapp.model.model_doctorList;

public class doctor_list extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        listView = findViewById(R.id.listView);
        this.setTitle("Select a doctor");

        ArrayList<model_doctorList> arrayList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("doctors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("loggingDoctorData", document.getId() + " => " + document.getData());
                        String uid = document.getId();
                        String name = document.getString("name");
                        String specialties = document.getString("specialties");
                        String accomplishment = document.getString("accomplishment");
                        arrayList.add(new model_doctorList(name, specialties, accomplishment, uid));

                    }
                    Log.d("arrayyy", arrayList.toString());
                    doctorListAdapter doctorAdapter = new doctorListAdapter(doctor_list.this, R.layout.activity_doctor_list_row, arrayList);
                    listView.setAdapter(doctorAdapter);
                }else Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                model_doctorList selectedDoctor = (model_doctorList) parent.getItemAtPosition(position);
                Log.d("selectedItem", selectedDoctor.toString());

                //Display appointment booking step..
                Intent intent = new Intent(doctor_list.this, stepper.class);
                intent.putExtra("selectedDoctor", selectedDoctor);
                startActivity(intent);
            }
        });
    }
}