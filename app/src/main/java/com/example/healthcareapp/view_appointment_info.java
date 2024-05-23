package com.example.healthcareapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcareapp.Firestore.FirestoreCallback;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.utils.Time;
import com.example.healthcareapp.videoCall.ui.LoginToCallActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class view_appointment_info extends AppCompatActivity {

    TextView date, time, doctorName, doctorPhone, consultationType, txtTitle;
    FirestoreManager fStoreManager;
    Button btnCall;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_info);

        this.setTitle("Upcoming Appointment");

        date = findViewById(R.id.txt_booking_date_text);
        time = findViewById(R.id.txt_booking_time_text);
        doctorName = findViewById(R.id.txt_booking_doctorName);
        doctorPhone = findViewById(R.id.txt_doctorPhone);
        consultationType = findViewById(R.id.txt_consultation_type);
        txtTitle = findViewById(R.id.txtTitle);
        btnCall = findViewById(R.id.btnCall);
        fStoreManager = new FirestoreManager();
        fAuth = FirebaseAuth.getInstance();
        init();
    }
    public void init(){
        String currentPatientUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fStoreManager.getADoc("patients", currentPatientUID, new FirestoreCallback() {
            @Override
            public void onFirestoreCallback_exists(Map<String, Object> data) {
                // Use the retrieved data here or pass it to another method
                String appointmentID =  data.get("appointmentID_patient").toString();
                if(appointmentID.equals("")) {
                    //this user doesnt book any appointment.`
                    txtTitle.setText("YOU HAVENT BOOK ANY APPOINTMENT");
                    btnCall.setEnabled(false);
                }else {
                    //this user have appointment
                    fStoreManager.getADoc("appointment", appointmentID,   new FirestoreCallback() {
                        @Override
                        public void onFirestoreCallback_exists(Map<String, Object> data) {
                            String datee = data.get("date").toString();
                            String timee = data.get("time").toString();
                            String consType = data.get("consultationType").toString();
                            String doctorUID = data.get("doctorUID").toString();
                            date.setText(datee);
                            time.setText(timee);
                            consultationType.setText(consType);

                            fStoreManager.getADoc("doctors", doctorUID, new FirestoreCallback() {
                                @Override
                                public void onFirestoreCallback_exists(Map<String, Object> data) {
                                    String doctorname = data.get("name").toString();
                                    doctorName.setText(doctorname);
                                    doctorPhone.setText(data.get("phone").toString());

                                    //below logic is to check if the current time is apointment time?
                                    String startTime = Time.convert12to24(Time.extractStartTime(timee));
                                    String endTime = Time.convert12to24(Time.extractEndTime(timee));
                                    int[] startDateTime = Time.extractDateTime(datee, startTime);
                                    int[] endDateTime = Time.extractDateTime(datee, endTime);
                                    LocalDateTime startDateTimee = LocalDateTime.of(startDateTime[0], startDateTime[1], startDateTime[2], startDateTime[3], startDateTime[4]);
                                    LocalDateTime endDateTimee = LocalDateTime.of(endDateTime[0], endDateTime[1], endDateTime[2], endDateTime[3], endDateTime[4]);
                                    LocalDateTime nowDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                                    if (consType.equals("On site Consultation")) {
                                        btnCall.setEnabled(false);
                                    } else if (!nowDateTime.isBefore(startDateTimee) && nowDateTime.isBefore(endDateTimee)) {
                                        //yes it is appointment time now.
                                        btnCall.setEnabled(true);
                                    } else {
                                        //no, now is not appointment time.
                                        btnCall.setEnabled(false);//TODO If u want to test VIDEO CALL function, comment this line, and uncomment the  next line.
//                                        btnCall.setEnabled(true);
                                    }

                                    btnCall.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(view_appointment_info.this, LoginToCallActivity.class);
                                            intent.putExtra("useruid", FirebaseAuth.getInstance().getCurrentUser().getUid()); //FirebaseAuth.getInstance().getCurrentUser().getUid() = patient uid.
                                            intent.putExtra("targetUseruid", doctorUID);
                                            intent.putExtra("username", fAuth.getCurrentUser().getDisplayName()); //it is patient name
                                            intent.putExtra("targetName", doctorname); //it is doctor name
                                            startActivity(intent);
                                        }
                                    });
                                }
                                @Override
                                public void onFirestoreCallback_notexists() {
                                }
                            });
                        }
                        @Override
                        public void onFirestoreCallback_notexists() {
                        }
                    });
                }}
            @Override
            public void onFirestoreCallback_notexists() {
            }
        });
    }
}
