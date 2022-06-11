package com.example.momobooklet_by_sm.manageuser
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.NavigationHost

import com.example.momobooklet_by_sm.database.ViewModel.UserViewModel
import com.example.momobooklet_by_sm.database.model.UserModel
import com.example.momobooklet_by_sm.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

private lateinit var  mUserViewModel: UserViewModel
    private lateinit var _binding : FragmentRegisterBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // Inflate the layout for this fragment

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
    // define  onclicklistener of submit button
    //  to take a UserModel object from the View view
    // store it in local database  , then use fields , Email and or phone to verify the existence of user ,

    //STEP 1
mUserViewModel = ViewModelProvider(this ).get(UserViewModel::class.java)

binding.submitBtn.setOnClickListener {


       AddUser()
        }

        return view
    }

    private fun AddUser() {

        //Get values in Editables
        val RegMoMoName=binding.registrationMomoName.text.toString()
        val Regphone = binding.registrationMomoPhone.text.toString()
        val RegEmail = binding.registrationEmailAddress.text.toString()
        val RegPass= binding.registrationPassword.text.toString()
        // Use values to create UserModel Object
        if( validator(RegMoMoName,RegEmail,RegPass,Regphone)){

             val user =UserModel(RegMoMoName,Regphone,RegEmail, RegPass,false)
          mUserViewModel.addUser(user)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            // Navigate Back
            (activity as NavigationHost).navigateTo(LoginFragment(), false) // Navigate to the next Fragment
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }




    }

    private fun validator (
     momoname : String ,

     email: String ,password:String

 ,momophone : String):Boolean{
     return !(TextUtils.isEmpty(momoname)&&
             TextUtils.isEmpty(email)&&TextUtils.isEmpty(password)&&TextUtils.isEmpty(momophone))

 }



}