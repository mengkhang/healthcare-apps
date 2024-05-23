package com.example.healthcareapp;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcareapp.Firestore.FirestoreCallback;
import com.example.healthcareapp.Firestore.FirestoreCallback_List;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.adapter.appointmentListAdapter;
import com.example.healthcareapp.model.model_appointmentList;
import com.example.healthcareapp.utils.LoadingDialog;
import com.example.healthcareapp.utils.Time;
import com.example.healthcareapp.utils.confirmationDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class view_appointment_list extends AppCompatActivity {
    ListView appointmentListView;
    FirestoreManager fStoreManager;
    Button btnCall;
    final LoadingDialog loadingDialog = new LoadingDialog(view_appointment_list.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_list);
        appointmentListView = findViewById(R.id.appointmentList);
        fStoreManager = new FirestoreManager();
        setAppointmentDataOnListView();
    }

    private void setAppointmentDataOnListView () {
        fStoreManager.getAllDocField_2Cont("appointment", "doctorUID", FirebaseAuth.getInstance().getCurrentUser().getUid(), "isCompleted", "no", new FirestoreCallback_List() {
            @Override
            public void onFirestoreCallback_List(ArrayList<Map<String, Object>> docDataList) {
                ArrayList<model_appointmentList> appointmentDataList = new ArrayList<>();
                for (Map<String, Object> data : docDataList) {
                    model_appointmentList model = new model_appointmentList();
                    model.setPatientUID(data.get("patientUID").toString());
                    model.setDoctorUID(data.get("doctorUID").toString());
                    model.setDate(data.get("date").toString());
                    model.setTime(data.get("time").toString());
                    model.setConsultationType(data.get("consultationType").toString());
                    model.setAppointmentID(data.get("documentID").toString()); //doc id is the appointment id.

                    String startTime = Time.convert12to24(Time.extractStartTime(data.get("time").toString()));
                    String endTime = Time.convert12to24(Time.extractEndTime(data.get("time").toString()));
                    String date = data.get("date").toString();
                    int[] extractedStartDateTime = Time.extractDateTime(date, startTime);
                    int[] extractedEndDateTime = Time.extractDateTime(date, endTime);
//                    Log.d("minute", extractedDateTime[4]+"");
                    model.setStartDateTime(LocalDateTime.of(extractedStartDateTime[0], extractedStartDateTime[1], extractedStartDateTime[2], extractedStartDateTime[3], extractedStartDateTime[4]));
                    model.setEndDateTime(LocalDateTime.of(extractedEndDateTime[0], extractedEndDateTime[1], extractedEndDateTime[2], extractedEndDateTime[3], extractedEndDateTime[4]));
//                    tempArr[4] = LocalDateTime.of(2022, 1, 1, 12, 0);
                    appointmentDataList.add(model);
                }
//                Log.d("time used for sorting: ",  ""+ appointmentDataList.get(0).getDateTime());
                for (int i = 0; i < appointmentDataList.size(); i++) {
                    final int index = i;
                    model_appointmentList model = appointmentDataList.get(i);
                    fStoreManager.getADoc("patients", model.getPatientUID(), new FirestoreCallback() {
                        @Override
                        public void onFirestoreCallback_exists(Map<String, Object> data_) {
                            model.setPatientName(data_.get("name").toString());
                            model.setPatientPhone(data_.get("phone").toString());
                            appointmentDataList.set(index, model);
                            if (index + 1 == appointmentDataList.size()) {// the loop will ended if this condition is true
                                ArrayList<model_appointmentList> sorted_appointmentList = sortAppointmentListBasedOnTime(appointmentDataList);
                                appointmentListAdapter adapter = new appointmentListAdapter(view_appointment_list.this , R.layout.activity_view_appointment_list_row, sorted_appointmentList, view_appointment_list.this);
                                appointmentListView.setAdapter(adapter);
                            }
                        }
                        @Override
                        public void onFirestoreCallback_notexists() {
                        }
                    });
                }
            }
        });
    }

    private ArrayList<model_appointmentList> sortAppointmentListBasedOnTime(ArrayList<model_appointmentList> appointmentDataList){
        //this function will sort the appointment based on dateTime, the earlier the appointment, the lower the index in the arraylist.
        ArrayList<model_appointmentList> sorted_appointmentList = new ArrayList<>();
        Collections.sort(appointmentDataList, Comparator.comparing(o -> ((LocalDateTime) o.getStartDateTime()).toString())); //.sort() will sort the list based on dateTime, 0[4] is the variable extractedDateTime
        for (model_appointmentList sorted_data : appointmentDataList) {
            sorted_appointmentList.add(sorted_data);
        }
        return sorted_appointmentList;
    }

    public void showConfirmationDialog(String patientUID, String appointmentID, String doctorUID, String date, String time) {
        confirmationDialog.show(view_appointment_list.this, "CONFIRMATION", "Are you sure you want to complete?", new confirmationDialog.ConfirmationListener() {
            @Override
            public void onConfirm() {
                completeAnAppointment(patientUID, appointmentID, doctorUID, date, time);
//                Log.d("tryitout");
            }
            @Override
            public void onCancel() {
                //do nothing
            }
        });
    }

    public void completeAnAppointment(String patientUID, String appointmentID, String doctorUID, String date, String time){
        //HERE GOES LOGIC FOR COMPLETE AN APPOINTMENT-
        // 1. it set patients/patientuid/appointmentID_patient to ""
        Map<String, Object> updates_appointmentID_patients = new HashMap<>();
        updates_appointmentID_patients.put("appointmentID_patient", "");
        Map<String, Object> updates_isCompleted = new HashMap<>();
        updates_isCompleted.put("isCompleted", "yes");
        Map<String, Object> updates_doctor = new HashMap<>();
        updates_doctor.put("unavailable_dateTime", FieldValue.arrayRemove(date+"/"+time));
        fStoreManager.updateField("patients", patientUID, updates_appointmentID_patients, new onUpdateListener() {
            @Override
            public void onUpdateListener_success() {
                // 2. it set appointment/appointmentID/isCompleted to "yes"
                fStoreManager.updateField("appointment", appointmentID, updates_isCompleted, new onUpdateListener() {
                    @Override
                    public void onUpdateListener_success() {
                        //3. it remove the appointment date time from doctors/doctortuid/unavailable_dateTime
                        fStoreManager.updateField("doctors", doctorUID, updates_doctor, new onUpdateListener() {
                            @Override
                            public void onUpdateListener_success() {
                                // 4. restart this activity to apply the changes.
                                loadingDialog.startLoadingDialog();
                                recreate();
                                loadingDialog.dismissDialog();
                            }

                            @Override
                            public void onUpdateListener_fail(Exception e) {
                                Log.e(TAG, "Error to update doctors/doctorUID/isCompleted to yes : ", e);
                            }
                        });
                    }
                    @Override
                    public void onUpdateListener_fail(Exception e) {
                        Log.e(TAG, "Error to update appointment/appointmentID/isCompleted to yes : ", e);
                    }
                });
            }
            @Override
            public void onUpdateListener_fail(Exception e) {
                Log.e(TAG, "Error to update patients/patientUID/appointmentID_patients to empty : ", e);
            }
        });
    }

}
