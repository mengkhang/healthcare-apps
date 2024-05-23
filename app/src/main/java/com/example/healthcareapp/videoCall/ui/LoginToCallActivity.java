package com.example.healthcareapp.videoCall.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcareapp.databinding.ActivityLoginToCallBinding;
import com.example.healthcareapp.utils.ToastUtils;
import com.example.healthcareapp.videoCall.repository.MainRepository;
import com.permissionx.guolindev.PermissionX;

public class LoginToCallActivity extends AppCompatActivity {

    private ActivityLoginToCallBinding views;

    private MainRepository mainRepository;
    String useruid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityLoginToCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());
        init();
    }

    private void init() {
        useruid = getIntent().getStringExtra("useruid");
        String username = getIntent().getStringExtra("username");
        String targetUseruid = getIntent().getStringExtra("targetUseruid");
        String targetName = getIntent().getStringExtra("targetName");

        views.username.setText(username);
        views.username.setEnabled(false);
        mainRepository = MainRepository.getInstance();
        views.enterBtn.setOnClickListener(v -> {
            PermissionX.init(this)
                    .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                    .request((allGranted, grantedList, deniedList) -> {
                        Log.d("username", username);
                        if(username.equals("")){
                            ToastUtils.showToast(getApplicationContext(), "Enter Username", Toast.LENGTH_SHORT);
                        } else if (allGranted) {
                            //login to firebase here
                            mainRepository.login(
                                    useruid, username, getApplicationContext(), () -> {
                                        //if success then we want to move to call activity
                                        Intent intent = new Intent(LoginToCallActivity.this, CallActivity.class);
                                        intent.putExtra("username", username);
                                        intent.putExtra("targetUseruid", targetUseruid); 
                                        intent.putExtra("targetName", targetName);
                                        startActivity(intent);
                                    }
                            );
                        }
                    });


        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //delete the useruid from database upon exit the activity_call screen
        mainRepository.exit(useruid);
    }
}