package com.example.healthcareapp.viewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareapp.R;
import com.example.healthcareapp.adapter.recycleviewInterface;

public class orderViewHolder extends RecyclerView.ViewHolder{
    public TextView txtOrderId, txtOrderDateTime, txtStatus_Time;

    public orderViewHolder(@NonNull View itemView, recycleviewInterface recycleviewInterface){
        super(itemView);
        txtOrderId = itemView.findViewById(R.id.txtOrderId);
        txtOrderDateTime = itemView.findViewById(R.id.txtOrderDateTime);
        txtStatus_Time = itemView.findViewById(R.id.txtStatus_Time);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleviewInterface.onItemClick(getAdapterPosition());
            }
        });
    }
}
