package com.example.healthcareapp.RealtimeDb;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.model.model_cartItem;
import com.example.healthcareapp.utils.ToastUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class realtimeDbManager {
    FirebaseDatabase firebaseDatabase;

    public realtimeDbManager() {
        firebaseDatabase = FirebaseDatabase.getInstance("https://healthcareapp-deba5-default-rtdb.asia-southeast1.firebasedatabase.app/");
    }

    public void addToCart(String patientUID, String medicineID, String medicineName, int quantity, String ringgit, String sen, Context context) {
        if(firebaseDatabase==null){
            throw new IllegalStateException("realtimeDbManager is not properly initialized, u havent create constructor.");
        }
        DatabaseReference cartRef = firebaseDatabase.getReference("carts").child(patientUID).child(medicineID);
        // Check if the item already exists in the cart
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the item exists, update the quantity
                    model_cartItem existingItem = dataSnapshot.getValue(model_cartItem.class);
                    if (existingItem != null) {
                        int newQuantity = existingItem.getQuantity() + quantity;
                        existingItem.setQuantity(newQuantity);
                        cartRef.setValue(existingItem);
                    }
                } else {
                    // If the item does not exist, create a new entry
                    double price = Double.parseDouble(ringgit + "." + sen) ;
                    double totalPrice = price * (double)quantity;
                    BigDecimal totalPriceFormatted = new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP); //format totalPrice to 2 decimal max.
                    model_cartItem cartItem = new model_cartItem(medicineID, medicineName, quantity, ringgit,sen, totalPriceFormatted.toString());
                    cartRef.setValue(cartItem);
                }
                ToastUtils.showToast(context, "Added successfully", Toast.LENGTH_SHORT);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Error to add carts to realtime database: "+ error.getMessage());
                ToastUtils.showToast(context, "Failed to add carts", Toast.LENGTH_SHORT);
            }
        });
    }

    public void viewCart(String path, String patientUID, realtimeDbCallback callbackList) {
        if(firebaseDatabase==null){
            throw new IllegalStateException("realtimeDbManager is not properly initialized, u havent create constructor.");
        }
        DatabaseReference cartRef = firebaseDatabase.getReference(path).child(patientUID);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Map<String, Object>> dataList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> dataMap = new HashMap<>();
                        String childName = snapshot.getKey();
                        dataMap.put("childName", childName); //this is child name in realtimeDb
//                        Log.d("tryyyyy", snapshot.getKey().toString());
                        for (DataSnapshot fieldSnapshot : snapshot.getChildren()) {
                            String fieldName = fieldSnapshot.getKey();
                            Object value = fieldSnapshot.getValue();
//                            Log.d("tryyyyy", fieldName + " / " +value.toString());
                            dataMap.put(fieldName, value); //this is the data value inside every child.
                        }
                        dataList.add(dataMap);
                    }
                    callbackList.onRealtimeDbCallback(dataList);
                } else {
                    // Handle the case where no data exists
                    Log.d("TAG", "No data available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors that occurred while trying to read the data
                Log.e("TAG", "Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    public void removeFromCart(String patientUID, String medicineID, Context mContext) {
        if(firebaseDatabase==null){
            throw new IllegalStateException("realtimeDbManager is not properly initialized, u havent create constructor.");
        }
        DatabaseReference cartRef = firebaseDatabase.getReference("carts").child(patientUID).child(medicineID);
        cartRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ToastUtils.showToast(mContext, "Item removed from cart", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastUtils.showToast(mContext, "Failed to remove item from cart", Toast.LENGTH_SHORT);
                    }
                });
    }

    public void removeAllCart(String patientUID, Context mContext, onUpdateListener onUpdateListener){
        if(firebaseDatabase==null){
            throw new IllegalStateException("realtimeDbManager is not properly initialized, u havent create constructor.");
        }
        DatabaseReference cartRef = firebaseDatabase.getReference("carts").child(patientUID);
        cartRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onUpdateListener.onUpdateListener_success();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onUpdateListener.onUpdateListener_fail(e);
            }
        });
    }
}
