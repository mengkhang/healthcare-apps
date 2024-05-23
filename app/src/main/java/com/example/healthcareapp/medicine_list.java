package com.example.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.Firestore.FirestoreCallback_List;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.adapter.medListAdapter;
import com.example.healthcareapp.model.model_doctorList;
import com.example.healthcareapp.model.model_medicine;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class medicine_list extends AppCompatActivity {

    FirestoreManager fStoreManager;
    FireStorageManager fStorageManager;
    ListView medicineListView;
    FloatingActionButton cartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);

        this.setTitle("Select a medicine");

        medicineListView = findViewById(R.id.medicineList);
        fStorageManager =  new FireStorageManager();
        fStoreManager = new FirestoreManager();
        cartBtn = findViewById(R.id.cartBtn);

        //get all medicine from firestore "medicine" collection
        fStoreManager.getAllDocField_noCont("medicine", new FirestoreCallback_List() {
            @Override
            public void onFirestoreCallback_List(ArrayList<Map<String, Object>> docDataList) {
                List<model_medicine> medicinesList = new ArrayList<>();
                for(Map<String, Object> data: docDataList){
                    model_medicine medicineModel = new model_medicine();
                    medicineModel.setMedName(data.get("documentID").toString()); //in medicine colection, the document id is equal to medicine Name.
                    medicineModel.setDes(data.get("des").toString());
                    medicineModel.setAvoid(data.get("avoid").toString());
                    medicineModel.setPriceRinggit(data.get("ringgit").toString());
                    medicineModel.setPriceSen(data.get("sen").toString());
                    medicineModel.setSuit(data.get("suit").toString());
                    medicinesList.add(medicineModel);
                }
                medListAdapter adapter = new medListAdapter(medicine_list.this, R.layout.activity_medicine_list_row, medicinesList);
                medicineListView.setAdapter(adapter);
            }
        });

        //when a medicine is clicked, handle here.
        medicineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                model_medicine selectedMedicine = (model_medicine) parent.getItemAtPosition(position);
                Log.d("selectedMedicine", selectedMedicine.toString());
                //Display appointment booking step..
                Intent intent = new Intent(medicine_list.this, medicine_details.class);
                intent.putExtra("selectedMedicine", selectedMedicine);
                startActivity(intent);
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(medicine_list.this, cart_list.class );
                startActivity(intent);
            }
        });
    }
}