package com.example.healthcareapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.healthcareapp.RealtimeDb.realtimeDbManager;
import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.model.model_medicine;
import com.example.healthcareapp.utils.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;

public class medicine_details extends AppCompatActivity {

    private model_medicine selectedMedicine;
    private ImageView medImg;
    private TextView txtMedName, txtdes, txtSuit, txtAvoid, txtPrice;
    private Button addToCartButton;
    private FireStorageManager fStorageManager;
    private realtimeDbManager realtimeDbManager;
    private final LoadingDialog loadingDialog = new LoadingDialog(this);
    private int selectedQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);
        this.setTitle("Medicine Details");
        selectedMedicine = (model_medicine) getIntent().getSerializableExtra("selectedMedicine");
        medImg = findViewById(R.id.medImg);
        txtMedName = findViewById(R.id.txtMedName);
        txtdes = findViewById(R.id.txtdes);
        txtSuit = findViewById(R.id.txtSuit);
        txtAvoid = findViewById(R.id.txtAvoid);
        txtPrice = findViewById(R.id.txtPrice);
        addToCartButton = findViewById(R.id.addToCartButton);
        fStorageManager = new FireStorageManager();
        realtimeDbManager = new realtimeDbManager();
        init();

    }

    public void init(){
        txtMedName.setText(selectedMedicine.getMedNameNo_());
        txtdes.setText(selectedMedicine.getDes());
        txtSuit.setText(selectedMedicine.getSuit());
        txtAvoid.setText(selectedMedicine.getAvoid());
        txtPrice.setText("Price: RM "+ selectedMedicine.getPriceRinggit() +"."+ selectedMedicine.getPriceSen());
        fStorageManager.setImageOnImageView(this, "medicine", selectedMedicine.getMedName(), medImg);
        medImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStorageManager.viewFullImage_fromFirebase(medicine_details.this, "medicine", selectedMedicine.getMedName());
            }
        });
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityDialog();
            }
        });
    }
    private void showQuantityDialog() {
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(20);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Quantity");
        builder.setView(numberPicker);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedQuantity = numberPicker.getValue();
                String patientUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                realtimeDbManager.addToCart(patientUID, selectedMedicine.getMedName(),
                        selectedMedicine.getMedNameNo_(), selectedQuantity,
                        selectedMedicine.getPriceRinggit(),
                        selectedMedicine.getPriceSen(),
                        medicine_details.this);
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

}