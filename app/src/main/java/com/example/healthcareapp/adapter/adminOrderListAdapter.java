package com.example.healthcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.R;
import com.example.healthcareapp.model.model_orderList;
import com.example.healthcareapp.viewHolder.orderViewHolder;

import java.util.List;

public class adminOrderListAdapter extends RecyclerView.Adapter<orderViewHolder> {

    Context context;
    List<model_orderList> items;
    private FireStorageManager fStorageManager =new FireStorageManager();
    private recycleviewInterface recycleviewInterface;

    public adminOrderListAdapter(Context context, List<model_orderList> items) {
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public orderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new orderViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_order_list_row,parent, false), recycleviewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull orderViewHolder holder, int position) {
        holder.txtOrderId.setText(items.get(position).getOrderID());
        holder.txtOrderDateTime.setText(items.get(position).getOrderDateTime() + " created");
        holder.txtStatus_Time.setText(items.get(position).getStatus() + "  --  updated by " + items.get(position).getLastStatusUpdate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener( recycleviewInterface clickListener){
        recycleviewInterface = clickListener;
    }
}
