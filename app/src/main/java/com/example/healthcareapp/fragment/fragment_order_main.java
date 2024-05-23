package com.example.healthcareapp.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.example.healthcareapp.Firestore.FirestoreCallback;
import com.example.healthcareapp.Firestore.FirestoreManager;
import com.example.healthcareapp.R;
import com.example.healthcareapp.adapter.orderFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class fragment_order_main extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    orderFragmentAdapter myViewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_main);

        this.setTitle("Order");
        String role = getIntent().getStringExtra("role");
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 =findViewById(R.id.view_pager);

        FirestoreManager fStoreManager = new FirestoreManager();
        myViewPagerAdapter = new orderFragmentAdapter(fragment_order_main.this, role);
        viewPager2.setAdapter(myViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
        // another way to restart this activity when onRestart(). (this activity)
    }
}