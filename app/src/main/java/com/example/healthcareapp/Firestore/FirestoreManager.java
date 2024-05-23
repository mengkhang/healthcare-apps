package com.example.healthcareapp.Firestore;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreManager {
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    public FirestoreManager() {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }

    public void updateUser_displayName(String displayName){
        if(fStore==null || fAuth==null){
            throw new IllegalStateException("FireStoreManager is not properly initialized, u havent create constructor.");
        }
        FirebaseUser currentUser =fAuth.getCurrentUser();
        if(currentUser != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                                Log.d("displayName", fAuth.getCurrentUser().getDisplayName());
                            }
                        }
                    });
        }
    }

    public void createDoc (String collection, String doc, Map<String, Object> data, onUpdateListener onUpdateListener){
        if(fStore==null || fAuth==null){
            throw new IllegalStateException("FireStoreManager is not properly initialized, u havent create constructor.");
        }
        fStore.collection(collection).document(doc).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void getADoc(String collection, String doc, FirestoreCallback callback){
        if(fStore==null || fAuth==null){
            throw new IllegalStateException("FireStoreManager is not properly initialized, u havent create constructor.");
        }
        fStore.collection(collection).document(doc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        Map<String, Object> docData = doc.getData();
                        callback.onFirestoreCallback_exists(docData);
                    }
                    else
                        callback.onFirestoreCallback_notexists();
                }else
                    Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public void getAllDocField_noCont (String collection, FirestoreCallback_List callback){
        if(fStore==null || fAuth==null){
            throw new IllegalStateException("FireStoreManager is not properly initialized, u havent create constructor.");
        }
        fStore.collection(collection)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Map<String, Object>> docDataList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> documentData = new HashMap<>();
                        documentData.put("documentID", document.getId());
                        documentData.putAll(document.getData()); //combine documentData map and document.getData() map together
                        docDataList.add(documentData);
                    }
                    callback.onFirestoreCallback_List(docDataList);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public void getAllDocField_1BoolCont (String collection,String whereEqualTo_field1, Boolean whereEqualTo_boolean1, FirestoreCallback_List callback){
        if(fStore==null || fAuth==null){
            throw new IllegalStateException("FireStoreManager is not properly initialized, u havent create constructor.");
        }
        fStore.collection(collection)
                .whereEqualTo(whereEqualTo_field1, whereEqualTo_boolean1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Map<String, Object>> documentDataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> documentData = new HashMap<>();
                                documentData.put("documentID", document.getId());
                                documentData.putAll(document.getData()); //combine documentData map and document.getData() map together
                                documentDataList.add(documentData);
                            }
                            callback.onFirestoreCallback_List(documentDataList);//add the combined map into the arraylist and return at callback.
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getAllDocField_2Cont (String collection,String whereEqualTo_field1, String whereEqualTo_value1 , String whereEqualTo_field2, String whereEqualTo_value2, FirestoreCallback_List callback){
        if(fStore==null || fAuth==null){
            throw new IllegalStateException("FireStoreManager is not properly initialized, u havent create constructor.");
        }
        fStore.collection(collection)
                .whereEqualTo(whereEqualTo_field1, whereEqualTo_value1)
                .whereEqualTo(whereEqualTo_field2, whereEqualTo_value2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Map<String, Object>> documentDataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> documentData = new HashMap<>();
                                documentData.put("documentID", document.getId());
                                documentData.putAll(document.getData()); //combine documentData map and document.getData() map together
                                documentDataList.add(documentData);
                            }
                            callback.onFirestoreCallback_List(documentDataList);//add the combined map into the arraylist and return at callback.
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void updateField (String collection, String doc ,Map<String, Object> updates, onUpdateListener updateListener){
        if(fStore==null || fAuth==null){
            throw new IllegalStateException("FireStoreManager is not properly initialized, u havent create constructor.");
        }
        fStore.collection(collection).document(doc).update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Update successful
                        if (updateListener != null) updateListener.onUpdateListener_success();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Update failed
                        if (updateListener != null)  updateListener.onUpdateListener_fail(e);
                    }
                });
    }

}



