package com.example.healthcareapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareapp.RealtimeDb.realtimeDbCallback;
import com.example.healthcareapp.RealtimeDb.realtimeDbManager;
import com.example.healthcareapp.Firestore.FirestoreCallback;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.adapter.cartListAdapter;
import com.example.healthcareapp.adapter.recycleviewInterface;
import com.example.healthcareapp.model.model_cartItem;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class cart_list extends AppCompatActivity{

    private realtimeDbManager realtimeDbManager;
    private Button checkoutBtn;
    private TextView txtTotalAmt;
    private RecyclerView cartRecycleView;
    private ArrayList<model_cartItem> cartList;
    private BigDecimal totalAmtFormatted;
    private final LoadingDialog loadingDialog = new LoadingDialog(cart_list.this);
    private ActivityResultLauncher<Intent> startPaymentForResult;
    private FirestoreManager fStoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        this.setTitle("Shopping Carts");
        cartRecycleView = findViewById(R.id.cartRecycleView);
        txtTotalAmt = findViewById(R.id.txtTotalAmt);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        realtimeDbManager = new realtimeDbManager();
        fStoreManager = new FirestoreManager();
        cartList = new ArrayList<>();
        getCartList();
        //set "start activity for result" for cart_list.java
        startPaymentForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result!=null && result.getResultCode()==RESULT_OK){
                    // Payment was successful, now you add order to firestore
                    createOrder();
                }else if(result.getResultCode() == RESULT_CANCELED){
                    // Payment was canceled or failed, handle accordingly
                    ToastUtils.showToast(cart_list.this, "payment cancelled or failed. Pls Try Again", Toast.LENGTH_SHORT);
                }
            }
        });
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCartEmpty()){
                    String[] parts = totalAmtFormatted.toString().split("\\.");
                    String ringgit = parts[0];
                    String sen = parts[1];
                    Intent intent = new Intent(cart_list.this, paymentGateway.class);
                    intent.putExtra("ringgit", ringgit);
                    intent.putExtra("sen", sen);
                    startPaymentForResult.launch(intent);
                }else
                    ToastUtils.showToast(cart_list.this, "Cart is empty. Add medicine before checking out",Toast.LENGTH_SHORT);
            }
        });
    }

    public void getCartList() {
        super.onStart();
        realtimeDbManager.viewCart("carts", FirebaseAuth.getInstance().getCurrentUser().getUid(), new realtimeDbCallback() {
            @Override
            public void onRealtimeDbCallback(ArrayList<Map<String, Object>> dataList) {
                double totalAmt = 0;
                for (Map<String, Object> data : dataList) {
                    String medicineID = data.get("childName").toString();
                    String medicineName = data.get("medicineName").toString();
                    int qty = Integer.parseInt(data.get("quantity").toString());
                    String ringgit = data.get("ringgit").toString();
                    String sen = data.get("sen").toString();
                    String totalPrice = data.get("totalPrice").toString();
                    totalAmt = totalAmt + Double.parseDouble(totalPrice);
                    cartList.add(new model_cartItem(medicineID, medicineName, qty, ringgit, sen, totalPrice));
                }
                totalAmtFormatted = new BigDecimal(totalAmt+10).setScale(2, RoundingMode.HALF_UP);//format totalAmt to 2 decimal max. //+10 to add rm10 delivery fee
                Log.d("totalAmtFormatted", totalAmtFormatted+"");
                txtTotalAmt.setText("Delivery Fees: RM10\nYour Total is: RM" + totalAmtFormatted);
                cartRecycleView.setLayoutManager(new LinearLayoutManager(cart_list.this));
                cartListAdapter cartListAdapter = new cartListAdapter(cart_list.this, cartList);
                cartRecycleView.setAdapter(cartListAdapter);
                cartListAdapter.setOnItemClickListener(new recycleviewInterface() {
                    @Override
                    //when orange dustbin button is click, delete the list from cart list.
                    public void onItemClick(int position) {
                        loadingDialog.startLoadingDialog();
                        realtimeDbManager.removeFromCart(FirebaseAuth.getInstance().getCurrentUser().getUid(), cartList.get(position).getMedicineID(), cart_list.this);
                        recreate(); //restart this activity
                        loadingDialog.dismissDialog();
                    }
                });
                checkoutBtn.setClickable(!isCartEmpty());
            }

        });
    }

    private boolean isCartEmpty() {
        // Assuming cartList is the list representing your cart items
        return cartList.isEmpty();
    }

    private void createOrder(){
        long timestampNow = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
        String timestampString = dateFormat.format(new Date(timestampNow));
        String uniqueOrderID = "order"+ UUID.randomUUID().toString().replace("-", "") + timestampString; //create a unique order id

        String patientUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> orderData = new HashMap<>();
        fStoreManager.getADoc("patients", patientUID, new FirestoreCallback() {
            @Override
            public void onFirestoreCallback_exists(Map<String, Object> data) {
                //1. map all field name and data
                orderData.put("deliverAddress", data.get("address").toString());
                orderData.put("isCompleted", false);
                orderData.put("lastStatusUpdate", Timestamp.now());
                ArrayList<Map<String, Object>> orderList = new ArrayList<>();
                for(int i =0; i<cartList.size(); i++){
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("medID", cartList.get(i).getMedicineID());
                    orderMap.put("medName", cartList.get(i).getMedicineName());
                    orderMap.put("price", cartList.get(i).getTotalPrice());
                    orderMap.put("qty", cartList.get(i).getQuantity());
                    orderList.add(orderMap);
                }
                orderData.put("order", orderList);
                orderData.put("orderDateTime", Timestamp.now());
                orderData.put("orderTotalAmount", totalAmtFormatted.toString() );
                orderData.put("patientUID", patientUID);
                orderData.put("receiverEmail", data.get("email").toString());
                orderData.put("receiverName", data.get("name").toString());
                orderData.put("receiverPhone", data.get("phone").toString());
                orderData.put("status", "Order Created");
                orderData.put("trackingNo", "");
                orderData.put("courier", "");
                //2. upload the map to firestore
                fStoreManager.createDoc("orders", uniqueOrderID, orderData, new onUpdateListener() {
                    @Override
                    public void onUpdateListener_success() {
                        clearCart(patientUID);
                    }

                    @Override
                    public void onUpdateListener_fail(Exception e) {
                        Log.e("Failed to create Order: ",e.getMessage());
                        ToastUtils.showToast(cart_list.this, "Failed to create Order", Toast.LENGTH_SHORT);
                    }
                });

            }
            @Override
            public void onFirestoreCallback_notexists() {
                ToastUtils.showToast(cart_list.this, "Patient UID not found", Toast.LENGTH_SHORT);
            }
        });
    }

    private void clearCart(String patientUID) {
        realtimeDbManager.removeAllCart(patientUID, cart_list.this, new onUpdateListener() {
            @Override
            public void onUpdateListener_success() {
                ToastUtils.showToast(cart_list.this, "Order Created", Toast.LENGTH_SHORT);
                startActivity(new Intent(cart_list.this, patient_profile.class));
                finish();
            }

            @Override
            public void onUpdateListener_fail(Exception e) {
                ToastUtils.showToast(cart_list.this, "Failed to clear cart. ", Toast.LENGTH_SHORT);
            }
        });
    }


}