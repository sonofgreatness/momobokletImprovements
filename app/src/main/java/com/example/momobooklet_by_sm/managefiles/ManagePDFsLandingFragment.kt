package com.example.momobooklet_by_sm.managefiles

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.momobooklet_by_sm.databinding.FragmentManagePDFsLandingBinding

class ManagePDFsLandingFragment : Fragment() {
    private var binding: FragmentManagePDFsLandingBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManagePDFsLandingBinding.inflate(inflater, container, false)
        binding!!.managepdfsDoorBtn.setOnClickListener { }
        binding!!.managepdfsDoorBtnCsv.setOnClickListener { }
        return binding!!.root
    }
}