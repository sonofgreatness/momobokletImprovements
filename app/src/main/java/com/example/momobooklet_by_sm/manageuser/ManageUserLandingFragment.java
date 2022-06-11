package com.example.momobooklet_by_sm.manageuser;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.momobooklet_by_sm.ManageUsers;
import com.example.momobooklet_by_sm.R;
import com.example.momobooklet_by_sm.databinding.FragmentManageUserLandingBinding;


public class ManageUserLandingFragment extends Fragment {
    private FragmentManageUserLandingBinding binding;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentManageUserLandingBinding.inflate(inflater,container,false);

        binding.usersDoorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(), ManageUsers.class);
                startActivity(i);
            }
        });


        View view = binding.getRoot() ;
        return view ;
    }
}