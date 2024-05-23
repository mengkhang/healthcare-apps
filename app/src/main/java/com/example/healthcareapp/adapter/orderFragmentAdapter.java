package com.example.healthcareapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.healthcareapp.fragment.fragment_order_list;

public class orderFragmentAdapter extends FragmentStateAdapter{

    String role;
    public orderFragmentAdapter(@NonNull FragmentActivity fragmentActivity, String role) {
        super(fragmentActivity);
        this.role = role;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new fragment_order_list("incomplete", role);
            case 1:
//                return new fragment_patient_order_list();
                return new fragment_order_list("completed", role);
            default:
                return new fragment_order_list("incomplete", role);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}