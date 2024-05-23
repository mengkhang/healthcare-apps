package com.example.healthcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareapp.RealtimeDb.realtimeDbManager;
import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.R;
import com.example.healthcareapp.model.model_cartItem;
import com.example.healthcareapp.viewHolder.cartListViewHolder;

import java.util.List;

public class cartListAdapter extends RecyclerView.Adapter<cartListViewHolder> {

    Context context;
    List<model_cartItem> items;
    private FireStorageManager fStorageManager =new FireStorageManager();
    private recycleviewInterface recycleviewInterface;

    public cartListAdapter(Context context, List<model_cartItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public cartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new cartListViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_cart_list_row,parent, false), recycleviewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull cartListViewHolder holder, int position) {
        int qtyNo = items.get(position).getQuantity();
        holder.medNameTxt.setText(items.get(position).getMedicineName());
        holder.qtyTxt.setText("Qty: " + qtyNo + "");
        holder.medPriceTxt.setText("RM " + items.get(position).getTotalPrice());
        fStorageManager.setImageOnImageView(context, "medicine", items.get(position).getMedicineID(), holder.medImg);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener( recycleviewInterface clickListener){
        recycleviewInterface = clickListener;
    }
}
