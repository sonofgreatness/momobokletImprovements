package com.example.momobooklet_by_sm.displaytransactions;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.momobooklet_by_sm.ManageUsers;
import com.example.momobooklet_by_sm.R;
import com.example.momobooklet_by_sm.databinding.FragmentDisplayTransactionsLandingBinding;

public class DisplayTransactionsLandingFragment extends Fragment {
    private FragmentDisplayTransactionsLandingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding=FragmentDisplayTransactionsLandingBinding.inflate(inflater,container,false);

        binding.showTransactionsDoorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(), DisplayTransactions.class);
                startActivity(i);
            }
        });
        return binding.getRoot();

    }
}