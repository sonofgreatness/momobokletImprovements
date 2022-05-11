package com.example.momobooklet_by_sm
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import com.example.momobooklet_by_sm.database.ViewModel.UserViewModel
import com.example.momobooklet_by_sm.database.model.UserModel
import com.example.shrinetutor2.LoginFragment
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*
import java.lang.Exception
import java.util.EnumSet.of
import java.util.List.of


class RegisterFragment : Fragment() {

private lateinit var  mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_register, container, false)

    // define  oncliclistener of submit button
    //  to take a UserModel object from the View view
    // store it in local database  , then use fields , Email and or phone to verify the existence of user ,

    //STEP 1

        view.submit_btn.setOnClickListener{
       AddUser()
        }

        return view
    }

    private fun AddUser() {

        //Get values in Editables
        val RegMoMoName= registration_momo_name.text.toString()
        val Regphone = registration_momo_phone.text.toString()
        val RegEmail = registration_email_address.text.toString()
        val RegPass= registration_password.text.toString()
        // Use values to create UserModel Object
        if( validator(RegMoMoName,RegEmail,RegPass,Regphone)){

             val user =UserModel(RegMoMoName,Regphone,RegEmail, RegPass)
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