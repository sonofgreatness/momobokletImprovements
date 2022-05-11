package com.example.shrinetutor2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register.*
import androidx.navigation.fragment.findNavController
import com.example.momobooklet_by_sm.NavigationHost
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.RegisterFragment
import kotlinx.android.synthetic.main.fragment_blank.view.*


class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_blank, container, false)


       val onClickListener = view.register_btn.setOnClickListener {

     //      findNavController().navigate(R.id.action_loginFragment_to_registerFragment)

           (activity as NavigationHost).navigateTo(RegisterFragment(), false) // Navigate to the next Fragment

       }





        return view
        }


}