package com.example.healthcareapp;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcareapp.FireStorage.FireStorageManager;
import com.example.healthcareapp.Firestore.FirestoreCallback;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.utils.ToastUtils;
import com.example.healthcareapp.utils.confirmationDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.example.healthcareapp.model.model_doctorList;

public class stepper extends AppCompatActivity {

    private LinearLayout containerLayout;
    private Button previousButton;
    private Button nextButton;
    private LinearLayout stepIndicatorsLayout;
    private int currentStep = 0;
    private View[] steps;
    private TextView[] stepIndicators;
    private model_doctorList selectedDoctor;
    private String selectedConsultationType, selectedTime, selectedDate;
    private FirestoreManager fStoreManager;
    private  ActivityResultLauncher<Intent> startPaymentForResult;
    private FireStorageManager fStorageManager;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepper);

        fStorageManager = new FireStorageManager();
        fStoreManager = new FirestoreManager();
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); //get current logged in user (the user logged in ofcoz is patient in the process of booking appointment)
        selectedDoctor = (model_doctorList) getIntent().getSerializableExtra("selectedDoctor");
        containerLayout = findViewById(R.id.container_layout);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        stepIndicatorsLayout = findViewById(R.id.step_indicators_layout);
        // Initializing steps (views)
        steps = new View[]{
                LayoutInflater.from(stepper.this).inflate(R.layout.activity_confirm_doctor_details, containerLayout, false),
                LayoutInflater.from(stepper.this).inflate(R.layout.activity_select_appointment_type, containerLayout,  false),
                LayoutInflater.from(stepper.this).inflate(R.layout.activity_select_appointment_date, containerLayout, false)
        };
        //set "start activity for result" for paymentGateway.java
        startPaymentForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result!=null && result.getResultCode()==RESULT_OK){
                    // Payment was successful, now you can submit the appointment
                    submitAppointment();
                }else if(result.getResultCode() == RESULT_CANCELED){
                    // Payment was canceled or failed, handle accordingly
                    ToastUtils.showToast(stepper.this, "payment cancelled or failed. Pls Try Again", Toast.LENGTH_SHORT);
                }
            }
        });
        selectedConsultationType = "";
        selectedTime = "";
        selectedDate="";


        // Initializing step indicators (circles and arrows) and show each step when click previous or next button
        initializeStepIndicators();
        showCurrentStep();

        // Seting click listener for previous button
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep > 0) {
                    currentStep--;
                    showCurrentStep();
                }
            }
        });

        // Seting click listener for next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep < steps.length - 1) {
                    currentStep++;
                    showCurrentStep();
                } else {
                    // When Last step reached submit the form
                    if(!selectedDate.equals("") && !selectedTime.equals("") && !selectedConsultationType.equals(""))
                        showConfirmationDialog();
                    else
                        ToastUtils.showToast(stepper.this, "Please Fill In All Details", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    // Initializing step indicators with circles and arrows
    private void initializeStepIndicators() {
        stepIndicators = new TextView[steps.length];
        for (int i = 0; i < steps.length; i++) {
            TextView stepIndicator = new TextView(this);
            stepIndicator.setText(String.valueOf(i + 1));
            stepIndicator.setTextColor(Color.WHITE);
            stepIndicator.setTextSize(18);
            stepIndicator.setBackgroundResource(R.drawable.circle_gray);
            stepIndicator.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            // The Margins are set like this
            // Left margin: 10 pixels
            // Top margin: 0 pixels (no margin)
            // Right margin: 10 pixels
            // Bottom margin: 0 pixels (no margin)
            params.setMargins(10, 0, 10, 0);
            stepIndicator.setLayoutParams(params);
            stepIndicatorsLayout.addView(stepIndicator);
            stepIndicators[i] = stepIndicator;

            if (i < steps.length - 1) {
                addArrowIndicator(stepIndicatorsLayout);
            }
        }
    }

    // Adding arrow indicator between step indicators
    private void addArrowIndicator(LinearLayout stepIndicatorsLayout) {
        ImageView arrow = new ImageView(this);
        // to add this create a new drawable resource file in res->drawable
        arrow.setImageResource(R.drawable.ic_arrow);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER_VERTICAL;
        arrow.setLayoutParams(params);
        stepIndicatorsLayout.addView(arrow);
    }

    // Showing the current step
    private void showCurrentStep() {
        containerLayout.removeAllViews();
        containerLayout.addView(steps[currentStep]);
        // If Current Step is greater then 0 then making Previous Button Visible
        previousButton.setVisibility(currentStep > 0 ? View.VISIBLE : View.INVISIBLE);
        nextButton.setText(currentStep < steps.length - 1 ? "Next" : "Checkout");
        updateStepIndicators();
//        Log.i("currentStep", Integer.toString(currentStep));
        if (currentStep==0){
            ImageView doctorImgView = findViewById(R.id.doctorImgView);
            TextView txtName = findViewById(R.id.txtNamee);
            TextView txtSpec = findViewById(R.id.txtSpecialties);
            TextView txtAccomplishment = findViewById(R.id.txtAccomplishmentt);
            txtName.setText(selectedDoctor.getName());
            txtSpec.setText(selectedDoctor.getSpecialties());
            txtAccomplishment.setText(selectedDoctor.getAccomplishment());
            fStorageManager.setImageOnImageView(stepper.this, "doctor", selectedDoctor.getUid(), doctorImgView);

        } else if(currentStep==1){
            String[] type = {"Online Consultation (RM30)", "On site Consultation (RM30)" };
            AutoCompleteTextView consultationType;
            ArrayAdapter<String> adapterItems;
            consultationType = findViewById(R.id.ConsltnType);
            adapterItems = new ArrayAdapter<String>(this, R.layout.appointment_type_item, type);
            consultationType.setAdapter(adapterItems);
            consultationType.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedConsultationType = adapterView.getItemAtPosition(i).toString().replaceAll("\\(RM30\\)", "");
                }
            });
        }else if(currentStep == 2){
            Log.d("selectedConsultationTyp", selectedConsultationType);
            DatePicker date = findViewById(R.id.datePicker);
            Calendar todayDate = Calendar.getInstance(); //get today date
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 14);//only allow user to select up to 14 days from today..
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDate = (selectedDate.equals("")) ? sdf.format(todayDate.getTime()) : selectedDate;
            date.setMinDate(todayDate.getTimeInMillis());
            date.setMaxDate(maxDate.getTimeInMillis());
            AutoCompleteTextView timeSlot;
            timeSlot = findViewById(R.id.timeSlot);
            //get time
            timeSlot.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedTime = adapterView.getItemAtPosition(i).toString();
                    Log.d("selectedTime", selectedTime);
                }
            });
            //get date on changes
            date.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                    String monthh = String.format("%02d", month +1);//implement leading zero for month and day
                    String dayy = String.format("%02d", day);
                    selectedDate = year + "-" + monthh + "-" + dayy;


                    fStoreManager.getADoc("doctors", selectedDoctor.getUid(), new FirestoreCallback() {
                        @Override
                        public void onFirestoreCallback_exists(Map<String, Object> data) {
                            ArrayList<String> dataa = (ArrayList<String>) data.get("unavailable_dateTime");
                            ArrayList<String> time = new ArrayList<>(Arrays.asList(
                                    "8:30 AM - 9:00 AM", "9:10 AM - 9:40 AM", "9:50 AM - 10:20 AM", "10:30 AM - 11:00 AM",
                                    "11:10 AM - 11:40 AM", "11:50 AM - 12:20 PM", "12:30 PM - 1:00 PM", "1:10 PM - 1:40 PM",
                                    "1:50 PM - 2:20 PM", "2:30 PM - 3:00 PM", "3:10 PM - 3:40 PM", "3:50 PM - 4:20 PM",
                                    "4:30 PM - 5:00 PM"
                            ));
                            for (String dateTime : dataa){
                                //dateTime = "2024-01-01/13:50 AM - 14:20 AM"
                                String[] date_time12h = dateTime.split("/");
                                String date = date_time12h[0].trim();
                                String time12h = date_time12h[1].trim();
                                if(selectedDate.equals(date)){
                                    time.remove(time12h);
                                }
                            }
                            ArrayAdapter<String> adapterItems;
                            adapterItems = new ArrayAdapter<String>(stepper.this, R.layout.appointment_type_item, time);
                            timeSlot.setAdapter(adapterItems);
                        }
                        @Override
                        public void onFirestoreCallback_notexists() {

                        }
                    });
                }
            });
        }
    }

    // Updating the step indicators to highlight the current step
    private void updateStepIndicators() {
        for (int i = 0; i < stepIndicators.length; i++) {
            if (i == currentStep) {
                stepIndicators[i].setBackgroundResource(R.drawable.circle_green);
            } else {
                stepIndicators[i].setBackgroundResource(R.drawable.circle_gray);
            }
        }
    }

    private void showConfirmationDialog() {
        confirmationDialog.show(stepper.this, "Confirm All Information Before Submit", "Are You Sure?", new confirmationDialog.ConfirmationListener() {
            @Override
            public void onConfirm() {
                startPayment();
            }
            @Override
            public void onCancel() {
                //do nothing
            }
        });
    }

    public void startPayment(){
        //start paymentGateway for result\
        Intent intent = new Intent(stepper.this, paymentGateway.class);
        intent.putExtra("ringgit", "30");
        intent.putExtra("sen", "00");
        startPaymentForResult.launch(intent);
    }

    // When clicked on submit button at last form/activity
    private void submitAppointment() {
        String appointmentID = "appointment"+UUID.randomUUID().toString().replace("-", ""); //create a unique appointment id
        Log.d("appointmentID", appointmentID);

        DocumentReference docRef_appointment = FirebaseFirestore.getInstance().collection("appointment").document(appointmentID);
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("date", selectedDate);
        appointment.put("time", selectedTime);
        appointment.put("doctorUID", selectedDoctor.getUid());
        appointment.put("patientUID", currentUser.getUid());
        appointment.put("consultationType", selectedConsultationType);
        appointment.put("isCompleted", "no");
        docRef_appointment.set(appointment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DocumentReference docRef_user_patient = FirebaseFirestore.getInstance().collection("patients").document(currentUser.getUid());
                docRef_user_patient.update("appointmentID_patient", appointmentID).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //add new unavailable date time into doctor fields
                        String unavailable_dateTime = selectedDate + "/" +selectedTime;
                            Map<String, Object> update = new HashMap<>();
                            update.put("unavailable_dateTime", FieldValue.arrayUnion(unavailable_dateTime));
                            fStoreManager.updateField("doctors", selectedDoctor.getUid(), update, new onUpdateListener() {
                                @Override
                                public void onUpdateListener_success() {
                                    //all data added, appointment booking finished
                                    startActivity(new Intent(getApplicationContext(), appointment_finish_page.class));
                                    finish();
                                }

                                @Override
                                public void onUpdateListener_fail(Exception e) {
                                    Log.d(TAG, "Fail to add unavailable_dateTime into doctors field: " + e.toString());
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Fail to add appointmentID into patients field : " + e.toString());
                        }
                    });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Fail to add appointment data into appointment collection: " + e.toString());
            }
        });
    }


}