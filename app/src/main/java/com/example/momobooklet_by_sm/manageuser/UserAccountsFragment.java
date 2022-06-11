package com.example.momobooklet_by_sm.manageuser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.momobooklet_by_sm.database.ViewModel.UserViewModel;
import com.example.momobooklet_by_sm.manageuser.UserProfileRecyclerViewAdapter;
import com.example.momobooklet_by_sm.database.model.UserModel;
import com.example.momobooklet_by_sm.databinding.FragmentUserAccountsBinding;

import java.util.ArrayList;
import java.util.List;


public class UserAccountsFragment extends Fragment {

UserViewModel mUserViewModel;
private FragmentUserAccountsBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        UserModel object1= new UserModel("ABC MOBILE MONEY","76464","hello@gmail.com","pass1",false);
        UserModel object2= new UserModel("ABC MOBILE MONEY","76911464","hello@gmail.com","pass1",true);



        List<UserModel> list =new ArrayList<UserModel>();

list.add(object1);
list.add(object2);
        binding = binding.inflate(inflater, container, false);

        // Set up the RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        UserProfileRecyclerViewAdapter adapter = new UserProfileRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);
        // add dummy data in database

        mUserViewModel = new ViewModelProvider(this ).get(UserViewModel.class);
        AddUsers();
        return binding.getRoot();
    }


    private void  AddUsers(){

        UserModel object1= new UserModel("ABC MOBILE MONEY","76464","hello@gmail.com","pass1",false);
        UserModel object2= new UserModel("ABC MOBILE MONEY","76911464","hello@gmail.com","pass1",true);


mUserViewModel.addUser(object1 );
mUserViewModel.addUser(object2);
        Toast.makeText(getContext(),"users added",Toast.LENGTH_SHORT).show();

    }
}