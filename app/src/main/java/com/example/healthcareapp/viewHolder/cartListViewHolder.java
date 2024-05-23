package com.example.healthcareapp.viewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareapp.R;
import com.example.healthcareapp.adapter.recycleviewInterface;

public class cartListViewHolder extends RecyclerView.ViewHolder {
    public ImageView medImg ;
    public TextView medNameTxt ;
    public TextView qtyTxt ;
    public TextView medPriceTxt;
    ImageButton deleteProductButton;

    public cartListViewHolder(@NonNull View itemView, recycleviewInterface recycleviewInterface){
        super(itemView);
        medImg = itemView.findViewById(R.id.productImageView);
        medNameTxt = itemView.findViewById(R.id.productNameTextView);
        qtyTxt = itemView.findViewById(R.id.qtyTxt);
        medPriceTxt = itemView.findViewById(R.id.productTotalPriceTextView);
        deleteProductButton = itemView.findViewById(R.id.deleteProductButton);
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleviewInterface.onItemClick(getAdapterPosition());
            }
        });
    }
}
