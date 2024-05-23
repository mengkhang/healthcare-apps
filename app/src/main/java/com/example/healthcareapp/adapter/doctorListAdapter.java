package com.example.healthcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.R;
import com.example.healthcareapp.model.model_doctorList;

import java.util.ArrayList;

public class doctorListAdapter extends ArrayAdapter<model_doctorList> {
    private Context mContext;
    private int mResource;
    private FireStorageManager fStorageManager;

    public doctorListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<model_doctorList> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.mResource=resource;
        fStorageManager = new FireStorageManager();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView doctorImageView = convertView.findViewById(R.id.image);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtSpec = convertView.findViewById(R.id.txtSpec);
        TextView txtAccomplishment = convertView.findViewById(R.id.txtAccomplishment);
        fStorageManager.setImageOnImageView(mContext, "doctor", getItem(position).getUid(), doctorImageView);

        txtName.setText(getItem(position).getName());
        txtSpec.setText(getItem(position).getSpecialties());
        txtAccomplishment.setText(getItem(position).getAccomplishment());
        return convertView;
    }
}
