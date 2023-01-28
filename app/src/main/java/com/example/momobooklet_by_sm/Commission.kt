package com.example.momobooklet_by_sm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.momobooklet_by_sm.databinding.FragmentCommissionBinding
import com.example.momobooklet_by_sm.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import com.example.momobooklet_by_sm.util.CommisionDatesManager


class Commission : Fragment() {


    private lateinit var _binding : FragmentCommissionBinding
    private val binding get() = _binding
    private lateinit var rootView:View
    private lateinit var  mTransactionViewModel:TransactionViewModel
    private lateinit var  mUserViewModel: UserViewModel
    private var datesManager = CommisionDatesManager()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCommissionBinding.inflate(inflater)
        rootView = binding.root
        mTransactionViewModel = (activity as MainActivity).mTransactionViewModel
        mUserViewModel = (activity as MainActivity).mUserViewModel

        setupdailyCommissionView()
        setupCompanyNameView()
        return rootView
    }


    private fun setupdailyCommissionView()
    {
        binding.dailyCommissionCard.commissionAnalysisTitleText.text = datesManager.generateTodayDate()
        mTransactionViewModel.dailyCommission.observe(viewLifecycleOwner) {
            binding.dailyCommissionCard.dailyCommissionText.text = it.toString().plus(" SZL")
         }
    }

   private fun setupCompanyNameView() {
        mUserViewModel._userInControl.observe(viewLifecycleOwner){
            binding.companyName.companyPhoneText.text = it[0].MoMoNumber
            binding.companyName.companyNameText.text = it[0].MoMoName
        }
    }

}