package com.example.healthcareapp.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.healthcareapp.Firestore.FirestoreCallback;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.Firestore.onUpdateListener;
import com.example.healthcareapp.R;
import com.example.healthcareapp.utils.ToastUtils;
import com.example.healthcareapp.utils.confirmationDialog;
import com.example.healthcareapp.model.model_appointmentList;
import com.example.healthcareapp.videoCall.ui.LoginToCallActivity;
import com.example.healthcareapp.view_appointment_list;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class appointmentListAdapter extends ArrayAdapter<model_appointmentList> {

    private Context context;
    private int resource;
    private FirestoreManager fStoreManager;
    private FirebaseAuth fAuth;
    private view_appointment_list activity;

    public
     appointmentListAdapter(@NonNull Context applicationContext, int resource, @NonNull ArrayList<model_appointmentList> appointmentDataList, view_appointment_list activity) {
        super(applicationContext, resource, appointmentDataList);
        this.context = applicationContext;
        this.resource = resource;
        fStoreManager = new FirestoreManager();
        fAuth = FirebaseAuth.getInstance();
        this.activity = activity; //to use public function from view_appointment_list activity class.
    }

    @NonNull
    @Override
    public View getView(int index, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);
        TextView textName = convertView.findViewById (R.id.textName);
        TextView textTime = convertView.findViewById(R.id.textTime);
//        TextView textAppointmentID = convertView.findViewById(R.id.textAppointmentID);
        TextView textDate = convertView.findViewById(R.id.textDate);
        TextView textConsultationType = convertView.findViewById(R.id.textType);
        TextView textPhone = convertView.findViewById(R.id.textPhone);
        Button btnCalll = convertView.findViewById(R.id.btnCalll);
        Button btnComplete = convertView.findViewById(R.id.btnComplete);

        String consType = getItem(index).getConsultationType();

        textDate.setText(getItem(index).getDate());
        textTime.setText(getItem(index).getTime());
        textName.setText(getItem(index).getPatientName());
        textConsultationType.setText(consType);
        textPhone.setText(getItem(index).getPatientPhone());
//        textAppointmentID.setText(getItem(index).getAppointmentID());

        if(consType.equals("On site Consultation")){
            btnCalll.setEnabled(false);
        }

        btnCalll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logic to check if now is within appointment time
                LocalDateTime startDateTime = getItem(index).getStartDateTime();
                LocalDateTime endDateTime = getItem(index).getEndDateTime();
                LocalDateTime nowDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

                if ((nowDateTime.isAfter(startDateTime) ||  nowDateTime.isEqual(startDateTime) ) && nowDateTime.isBefore(endDateTime)) {
                    // yes now is within appointment timme
                    Intent intent = new Intent(context, LoginToCallActivity.class);
                    intent.putExtra("useruid", FirebaseAuth.getInstance().getCurrentUser().getUid()); //it is doctor uid
                    intent.putExtra("targetUseruid", getItem(index).getPatientUID());//it is patient uid
                    intent.putExtra("username", fAuth.getCurrentUser().getDisplayName());//it is doctor name
                    intent.putExtra("targetName", getItem(index).getPatientName()); //it is patient name
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d("displayName", currentUser.getDisplayName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //add this line to start an activity outside of the activity class itself.
                    context.startActivity(intent);
                }else {
                    // no the apointment time is over.
                    ToastUtils.showToast(context, "Now is not appointment time", Toast.LENGTH_SHORT);
                    //TODO if u want to test VIDEO CALL function, comment the upper ToastUtils line, and uncomment the below line
//                    Intent intent = new Intent(context, LoginToCallActivity.class);
//                    intent.putExtra("useruid", FirebaseAuth.getInstance().getCurrentUser().getUid()); //it is doctor uid
//                    intent.putExtra("targetUseruid", getItem(index).getPatientUID());//it is patient uid
//                    intent.putExtra("username", fAuth.getCurrentUser().getDisplayName());//it is doctor name
//                    intent.putExtra("targetName", getItem(index).getPatientName()); //it is patient name
//                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                    Log.d("displayName", currentUser.getDisplayName());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //add this line to start an activity outside of the activity class itself.
//                    context.startActivity(intent);
                    //TODO till here-------------------------------------------------------------------------------------------------------
                }
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logic to check if the now time is after the appointment start time.
                LocalDateTime startDateTimee = getItem(index).getStartDateTime();
                Log.d("startDateTime", startDateTimee+"");
                LocalDateTime nowDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                if (nowDateTime.isAfter(startDateTimee) || nowDateTime.isEqual(startDateTimee)) {
                    // yes the appointment have started, below is logic for completing appointment
                    activity.showConfirmationDialog(getItem(index).getPatientUID(), getItem(index).getAppointmentID(), getItem(index).getDoctorUID(), getItem(index).getDate(), getItem(index).getTime());
                }else{
                    // no, the appointment haven't started
                    Log.d("testDateTime", getItem(index).getDate() + "/"+getItem(index).getTime());
                    ToastUtils.showToast(context, "Appointment havent start", Toast.LENGTH_SHORT);//TODO: if u want to test DOCTOR - COMPLETE AN APPOINTMENT feature, comment this line, and uncomment next line.
//                    activity.showConfirmationDialog(getItem(index).getPatientUID(), getItem(index).getAppointmentID(), getItem(index).getDoctorUID(), getItem(index).getDate(), getItem(index).getTime());

                }
            }
        });
        return convertView;
    }
}