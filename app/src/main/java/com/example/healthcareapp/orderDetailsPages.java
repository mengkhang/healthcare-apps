package com.example.healthcareapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.model.model_orderList;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class orderDetailsPages extends AppCompatActivity {

    TextView txtOrderId, txtReceiverName, txtReceiverPhone, txtOrder, txtTotalAmount, txtOrderTime, txtEmail, txtOrderStatus, txtLastStatus, txtTrackingNo, txtCourier;
    Button updateBtn, saveBtn;
    model_orderList selectedOrder;
    Boolean isCompleted;
    String role;
    FirestoreManager fStoreManager;
    final LoadingDialog loadingDialog = new LoadingDialog(orderDetailsPages.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_pages);

        fStoreManager= new FirestoreManager();
        txtOrderId = findViewById(R.id.txtOrderId);
        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtReceiverPhone = findViewById(R.id.txtReceiverPhone);
        txtOrder = findViewById(R.id.txtOrder);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtOrderTime = findViewById(R.id.txtOrderTime);
        txtEmail = findViewById(R.id.txtEmail);
        txtOrderStatus = findViewById(R.id.txtOrderStatus);
        txtOrderStatus = findViewById(R.id.txtOrderStatus);
        txtLastStatus = findViewById(R.id.txtLastStatus);
        updateBtn = findViewById(R.id.updateBtn);
        saveBtn = findViewById(R.id.saveBtn);
        txtTrackingNo = findViewById(R.id.txtTrackingNo);
        txtCourier = findViewById(R.id.txtCourier);

        selectedOrder =(model_orderList)  getIntent().getSerializableExtra("selectedOrder");
        isCompleted = getIntent().getBooleanExtra("isCompleted", false);
        role = getIntent().getStringExtra("role");
        txtOrderId.setText(selectedOrder.getOrderID());
        txtReceiverName.setText(selectedOrder.getReceiverName());
        txtReceiverPhone.setText(selectedOrder.getReceiverPhone());

        if(isCompleted || role.equals("patient")){
           updateBtn.setVisibility(View.INVISIBLE);
           saveBtn.setVisibility(View.INVISIBLE);
        }
        ArrayList<Map<String, Object>> order = selectedOrder.getOrderArray();
        String orderText = "";
        for (int i = 0; i<order.size();i++){
            String medID = order.get(i).get("medID").toString();
            String medName = order.get(i).get("medName").toString();
            String price = order.get(i).get("price").toString();
            String qty = order.get(i).get("qty").toString();
            orderText = orderText + (i+1 +". "+medName+" - "+qty +" (RM" + price+")\n");
        }
        txtOrder.setText(orderText);
        txtTotalAmount.setText(selectedOrder.getOrderTotalAmount());
        txtOrderTime.setText(selectedOrder.getOrderDateTime());
        txtEmail.setText(selectedOrder.getReceiverEmail());
        txtOrderStatus.setText(selectedOrder.getStatus());
        txtLastStatus.setText(selectedOrder.getLastStatusUpdate());
        txtCourier.setText(selectedOrder.getCourier());
        txtTrackingNo.setText(selectedOrder.getTrackingNo());
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                saveUpdate(txtOrderStatus.getText().toString(), txtTrackingNo.getText().toString(), txtCourier.getText().toString());
            }
        });

    }

    private void showUpdateDialog() {
        final String[] orderStatusOptions = {
                "Order Created",
                "Preparing Parcel",
                "Handover Parcel to Courier",
                "Delivered",
                "Completed"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Order Status")
                .setItems(orderStatusOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String selectedDeliveryStatus = orderStatusOptions[i];
                        if (selectedDeliveryStatus.equals("Handover Parcel to Courier")) {
                            showTrackingInputDialog(selectedDeliveryStatus);
                        } else {
                            txtOrderStatus.setText(selectedDeliveryStatus);
                            ToastUtils.showToast(orderDetailsPages.this, "Updated", Toast.LENGTH_SHORT);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTrackingInputDialog(String selectedDeliveryStatus) {
        AlertDialog.Builder trackingNumberBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_tracking_order_input, null);
        trackingNumberBuilder.setView(dialogView);
        final EditText TrackingNoInput = dialogView.findViewById(R.id.TrackingNoInput);
        final EditText courierServiceInput = dialogView.findViewById(R.id.courierServiceInput);

        trackingNumberBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String trackingNumber = TrackingNoInput.getText().toString();
                String courierService = courierServiceInput.getText().toString();
                if(TextUtils.isEmpty(trackingNumber) || TextUtils.isEmpty(courierService)){
                    ToastUtils.showToast(orderDetailsPages.this, "Enter field", Toast.LENGTH_SHORT);
                    showTrackingInputDialog(selectedDeliveryStatus);
                }else{
                    txtOrderStatus.setText(selectedDeliveryStatus);
                    txtTrackingNo.setText(trackingNumber);
                    txtCourier.setText(courierService);
                    ToastUtils.showToast(orderDetailsPages.this, "Updated", Toast.LENGTH_SHORT);
                }

            }
        });

        trackingNumberBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog trackingNumberDialog = trackingNumberBuilder.create();
        trackingNumberDialog.show();
    }

    private void saveUpdate(String deliveryStatus, String trackingNumber, String courierService) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", deliveryStatus);
        if(!trackingNumber.equals(""))   updateMap.put("trackingNo", trackingNumber);
        if(!courierService.equals(""))  updateMap.put("courier", courierService);
        updateMap.put("lastStatusUpdate", Timestamp.now());
        if(deliveryStatus.equals("Completed")) updateMap.put("isCompleted", true);
        fStoreManager.updateField("orders", selectedOrder.getOrderID(), updateMap, new onUpdateListener() {
            @Override
            public void onUpdateListener_success() {
                loadingDialog.dismissDialog();
                ToastUtils.showToast(orderDetailsPages.this, "update Successful", Toast.LENGTH_SHORT);
                finish();
            }

            @Override
            public void onUpdateListener_fail(Exception e) {
                loadingDialog.dismissDialog();
                ToastUtils.showToast(orderDetailsPages.this, "update Failed", Toast.LENGTH_SHORT);
            }
        });
    }
}