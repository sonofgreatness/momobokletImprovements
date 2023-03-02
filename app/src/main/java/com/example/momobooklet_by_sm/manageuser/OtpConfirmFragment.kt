package com.example.momobooklet_by_sm.manageuser

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.databinding.FragmentOtpConfirmBinding
import com.example.momobooklet_by_sm.ui.viewmodels.UserViewModel
import com.example.momobooklet_by_sm.util.Constants
import com.example.momobooklet_by_sm.util.classes.AddUserAcc
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber


class OtpConfirmFragment : Fragment() {
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
    private val auth : FirebaseAuth =FirebaseAuth.getInstance()
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

    /*  requestPermissionLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        // Permission is granted
                        mySMS_ACTION_HANDLER =SMS_ACTION_HANDLER.DETECT_SMS
                        initiatePhoneAuthRegistration(mySMS_ACTION_HANDLER)
                    } else {
                        mySMS_ACTION_HANDLER =SMS_ACTION_HANDLER.READ_EDITTEXTS
                        initiatePhoneAuthRegistration(mySMS_ACTION_HANDLER)
                    }
               }
     */

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOtpConfirmBinding.inflate(inflater, container,false)
        mUserViewModel = ViewModelProvider(this )[UserViewModel::class.java]
        binding.otpSubtitleUsermobile.text = requireArguments().getString(Constants.PHONE_NUMBER_KEY)
        rootView = binding.root
        RegForReal = AddUserAcc(requireArguments().getString(Constants.PHONE_NUMBER_KEY)!!,requireActivity(),auth)


        setupUpNavigationOnClick()
        setupSubmitButtonOnClick()
    return rootView
    }


    private fun makeSubmitButtonVisible() {
        binding.otpEnterBtn.visibility = View.VISIBLE
        binding.otpEnterBtn.isEnabled = true
    }
    private fun setupSubmitButtonOnClick() {
        binding.otpEnterBtn.setOnClickListener {
            smsString = binding.otpEditText1.text.toString()
            RegForReal.setSMS(smsString)
            RegForReal.signInwithSms()
            Toast.makeText(requireContext(),"SuccessFull Registration", Toast.LENGTH_SHORT).show()
            it.findNavController().navigate(R.id.action_otpConfirmFragment_to_userAccountsFragment)

        }
    }
    private fun setupUpNavigationOnClick() {
        binding.appBarOtp.setOnClickListener {
            it.findNavController().navigate(R.id.action_otpConfirmFragment_to_registerFragment)
        }
    }

/*
    private fun check_for_permission_and_request() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Toast.makeText(requireContext(), "Granted In Legacy",Toast.LENGTH_SHORT).show()
                mySMS_ACTION_HANDLER =SMS_ACTION_HANDLER.DETECT_SMS
                initiatePhoneAuthRegistration(mySMS_ACTION_HANDLER)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) ->{
            // An educational UI, that explains to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
                showRequestPermissionsInfoAlertDialog()
        }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_SMS)
            }
        }
    }


    private fun showRequestPermissionsInfoAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(R.string.permission_alert_dialog_title) // Your own title
        builder.setMessage(R.string.permission_dialog_message) // Your own message
        builder.setPositiveButton(
            R.string.action_ok
        ) { dialog, which ->
            dialog.dismiss()
            requestPermissionLauncher.launch(
                Manifest.permission.READ_SMS)
            // Display system runtime permission request?
        }
        builder.setNegativeButton(
            R.string.action_no_thanks
        ) { dialog, which ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }


    private fun initiatePhoneAuthRegistration(mysmsActionHandler: SMS_ACTION_HANDLER) {
        if (mysmsActionHandler == SMS_ACTION_HANDLER.DETECT_SMS)
                 autoReadSmS()
        if (mysmsActionHandler == SMS_ACTION_HANDLER.READ_EDITTEXTS)
            readSmSFromEditTexts()
    }



    private fun autoReadSmS()
    {
        makeSubmitButtonInvisible()
        RegForReal = AddUserAcc(requireArguments().getString(Constants.PHONE_NUMBER_KEY)!!,requireActivity(),auth)


        mUserViewModel.registrationSmSBody.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            if(!isEmpty(it))
            {
                RegForReal.setSMS(it)
                RegForReal.signInwithSms()
            }
            Timber.d("Failed SMS String Value  : -> $it")
        }
    }
    private fun makeSubmitButtonInvisible() {
        binding.otpEnterBtn.visibility = View.GONE
        binding.otpEnterBtn.isEnabled = false
    }
    private fun readSmSFromEditTexts()
    {
        makeSubmitButtonVisible()
        RegForReal = AddUserAcc(requireArguments().getString(Constants.PHONE_NUMBER_KEY)!!,requireActivity(),auth)

    }*/

}