package com.example.momobooklet_by_sm.presentation.manageuser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.MainActivity2
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.databinding.FragmentOtpConfirmBinding
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import com.example.momobooklet_by_sm.common.util.Constants
import com.example.momobooklet_by_sm.common.util.classes.AddUserAcc
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpConfirmFragment2 : Fragment() {
    /*Object of this class will determine which action to take
    * in completing User Sign In
    * if DETECT_SMS -> we automatically detect sms read the OTP string for user
    * if READ_EDITTEXT -> user will fill edit Texts with sms data manually.
    * */
    enum class SMS_ACTION_HANDLER{
        DETECT_SMS,
        READ_EDITTEXTS;
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var _binding: FragmentOtpConfirmBinding
    private val  binding get() = _binding
    private lateinit  var  rootView: View
    private var smsString: String = "0000"
    private lateinit var RegForReal: AddUserAcc
    private lateinit var  mUserViewModel: UserViewModel

    //private lateinit var   requestPermissionLauncher : ActivityResultLauncher
    private  lateinit var mySMS_ACTION_HANDLER: SMS_ACTION_HANDLER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOtpConfirmBinding.inflate(inflater, container,false)
        mUserViewModel = (activity as MainActivity2).mUserViewModel
        binding.otpSubtitleUsermobile.text = requireArguments().getString(Constants.PHONE_NUMBER_KEY)
        rootView = binding.root
        RegForReal = AddUserAcc(requireArguments().getString(Constants.PHONE_NUMBER_KEY)!!,requireActivity(),auth)

        setupUpNavigationOnClick()
        waitThenEnableSubmitButton()
        setupSubmitButtonOnClick()
        return rootView
    }

    private fun waitThenEnableSubmitButton() {
        binding.otpEnterBtn.isEnabled = false
        binding.otpEnterBtn.visibility = View.GONE

        lifecycleScope.launch {
            delay(2000)
            binding.otpEnterBtn.isEnabled =  true
            binding.otpEnterBtn.visibility = View.VISIBLE

        }
    }

    private fun setupSubmitButtonOnClick() {
        binding.otpEnterBtn.setOnClickListener {
            smsString = binding.otpEditText1.text.toString()
            RegForReal.setSMS(smsString)
            RegForReal.signInwithSms()
            Toast.makeText(requireContext(),"Registered", Toast.LENGTH_SHORT).show()
            moveToMainActivity()
        }
    }

    /***************************************************************
     * moveToMainActivity() -> starts MainActivity
     *************************************************************/
    private fun moveToMainActivity() {
            val i = Intent(requireContext(), MainActivity::class.java)
            startActivity(i)
    }

    private fun setupUpNavigationOnClick() {
        binding.appBarOtp.setOnClickListener {
            it.findNavController().navigate(R.id.action_otpConfirmFragment2_to_registerFragment2)
        }
    }
}