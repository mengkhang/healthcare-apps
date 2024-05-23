package com.example.healthcareapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.R;
import com.example.healthcareapp.medicine_list;
import com.example.healthcareapp.model.model_medicine;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class medListAdapter extends ArrayAdapter<model_medicine> {

    private Context context;
    private int resource;
    private FireStorageManager fStorageManager;
    private medicine_list medicine_list;

    public medListAdapter(Context context, int resource, List<model_medicine> medicines) {
        super(context, resource, medicines);
        this.context = context;
        this.resource = resource;
        fStorageManager = new FireStorageManager();
    }

    @NonNull
    @Override
    public View getView(int index, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);
        ImageView medicineImageView = convertView.findViewById(R.id.medicineImageView);
        TextView medicineNameTextView = convertView.findViewById(R.id.medicineNameTextView);
        TextView priceTxt = convertView.findViewById(R.id.priceTxt);
        medicineNameTextView.setText(getItem(index).getMedNameNo_());
        priceTxt.setText("RM " +getItem(index).getPriceRinggit() + "."+getItem(index).getPriceSen());
        fStorageManager.setImageOnImageView(context, "medicine", getItem(index).getMedName(), medicineImageView);
        return convertView;
    }
}
