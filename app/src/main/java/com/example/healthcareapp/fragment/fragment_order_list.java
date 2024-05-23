package com.example.healthcareapp.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthcareapp.Firestore.FirestoreCallback_List;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.R;
import com.example.healthcareapp.adapter.adminOrderListAdapter;
import com.example.healthcareapp.adapter.recycleviewInterface;
import com.example.healthcareapp.orderDetailsPages;
import com.example.healthcareapp.model.model_orderList;
import com.example.healthcareapp.utils.LoadingDialog;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Map;

public class fragment_order_list extends Fragment {

    Context this_context;
    RecyclerView adminOrderRecycleView;
    String tab, role;
    private LoadingDialog loadingDialog;
    private FirestoreManager fStoreManager;

    public fragment_order_list(){
    }
    public fragment_order_list(String tab, String role){
        this.tab = tab;
        this.role = role;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this_context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        fStoreManager = new FirestoreManager();
        adminOrderRecycleView = view.findViewById(R.id.adminOrderRecycleView);
        if(tab.equals("incomplete"))
            init_incomplete(view);
        else
            init_completed(view);

        return view;

    }

    public void init_incomplete(View view){
        getOrderList(false);
    }

    public void init_completed(View view){
        getOrderList(true);
    }

    public void getOrderList(Boolean isCompleted){
        ArrayList<model_orderList> orderList = new ArrayList<>();
        fStoreManager.getAllDocField_1BoolCont("orders", "isCompleted", isCompleted, new FirestoreCallback_List() {
            @Override
            public void onFirestoreCallback_List(ArrayList<Map<String, Object>> docDataList) {
                for (Map<String, Object> data: docDataList){
                    String orderID = data.get("documentID").toString();
                    Timestamp lastStatusUpdate = (Timestamp) data.get("lastStatusUpdate");
                    Timestamp orderDateTime = (Timestamp) data.get("orderDateTime");
                    ArrayList<Map<String, Object>> orderArrays = (ArrayList<Map<String, Object>>) data.get("order");
                    String orderTotalAmount = data.get("orderTotalAmount").toString();
                    String patientUID = data.get("patientUID").toString();
                    String receiverEmail = data.get("receiverEmail").toString();
                    String receiverName = data.get("receiverName").toString();
                    String receiverPhone = data.get("receiverPhone").toString();
                    String status = data.get("status").toString();
                    String courier = data.get("courier").toString();
                    String trackingNo = data.get("trackingNo").toString();
                    Boolean isCompleted = (Boolean) data.get("isCompleted");
                    orderList.add(new model_orderList(orderID, orderTotalAmount, patientUID, receiverEmail, receiverName, receiverPhone, status, isCompleted, orderArrays, orderDateTime, lastStatusUpdate, courier, trackingNo));
                }

                adminOrderRecycleView.setLayoutManager(new LinearLayoutManager(this_context));
                adminOrderListAdapter adminOrderListAdapter = new adminOrderListAdapter(this_context, orderList);
                adminOrderRecycleView.setAdapter(adminOrderListAdapter);
                adminOrderListAdapter.setOnItemClickListener(new recycleviewInterface() {
                    @Override
                    public void onItemClick(int position) {
                        model_orderList selectedOrder = orderList.get(position); //get selected index of the list that user clicked . find that index in the arrayList inCompleted_OrderList.
                        Intent intent = new Intent(this_context, orderDetailsPages.class);
                        intent.putExtra("selectedOrder", selectedOrder);
                        intent.putExtra("isCompleted", isCompleted); //position is equals to the index in the "orderList" arrayList
                        intent.putExtra("role", role);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        this_context.startActivity(intent);
                    }
                });

            }
        });
    }
}