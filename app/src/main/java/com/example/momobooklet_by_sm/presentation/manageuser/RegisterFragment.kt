package com.example.momobooklet_by_sm.presentation.manageuser

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.Constants
import com.example.momobooklet_by_sm.common.util.Constants.Companion.COUNTRY_CODE
import com.example.momobooklet_by_sm.common.util.Constants.Companion.PHONE_NUMBER_KEY
import com.example.momobooklet_by_sm.common.util.classes.DrawableSpan
import com.example.momobooklet_by_sm.data.local.models.UserModel
import com.example.momobooklet_by_sm.databinding.FragmentRegisterBinding
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth


class RegisterFragment : Fragment() {


    private lateinit  var  mAuth: FirebaseAuth
    private lateinit var  drawableSpan: DrawableSpan
    private lateinit var  mUserViewModel: UserViewModel
    private lateinit var _binding : FragmentRegisterBinding
    private lateinit var rootView:View
    private val binding get() = _binding
    private var passwordEndIconChangeHelper = true
    private var mBundle:Bundle = Bundle()



  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

            mAuth = FirebaseAuth.getInstance()
            _binding = FragmentRegisterBinding.inflate(inflater, container, false)
            rootView = binding.root
            setupPasswordTextinput()
            mUserViewModel = (activity as MainActivity).mUserViewModel
            setUpController()
      drawableSpan= DrawableSpan(binding.submitBtn, marginStart = 20, progressDrawable = CircularProgressDrawable(binding.submitBtn.context))
      binding.submitBtn.setOnClickListener {
            addUser(it)
        }
      return rootView
    }
    private fun setupPasswordTextinput() {
        binding.apply {
            passwordInputLayout.setEndIconOnClickListener {
                passwordEndIconChangeHelper = if (passwordEndIconChangeHelper) {
                    passwordInputLayout.setEndIconDrawable(R.drawable.password_icon)
                   // passwordInputLayout.endIconMode = END_ICON_PASSWORD_TOGGLE
                    false
                } else {
                    passwordInputLayout.setEndIconDrawable(R.drawable.password_icon_visibility_off)
                   // passwordInputLayout.endIconMode = END_ICON_PASSWORD_TOGGLE
                    true
                }
            }
        }
    }
    /* Handles back Navigation by top arrow*/
    private fun setUpController() {
        binding.toolbarShowTransactions.setNavigationOnClickListener {
            if (requireArguments().getString(Constants.REGISTRATION_HOME_KEY)!! == "commission")
            it!!.findNavController().navigate(R.id.action_registerFragment_to_commission)
            else
                moveToUserAccountsFragment(it)
        }
    }

    private fun addUser(view: View) {
        //Get values in EditTexts
        val regMoMoName=binding.registrationMomoName.text.toString()
        val regphone = binding.registrationMomoPhone.text.toString()
        val regEmail = binding.registrationEmailAddress.text.toString()
        val regPass= binding.registrationPassword.text.toString()
        // Use values to create UserModel Object
        if(!(validator(regMoMoName,regEmail,regPass,regphone))){
                if(checkifPhoneisValid()){
                    val user = UserModel(regMoMoName, regphone, regEmail, regPass, true,false)
                    mUserViewModel.addUser(user)
                    changeTextToProgressbar(view)
                    mBundle.putString(PHONE_NUMBER_KEY, COUNTRY_CODE.plus(regphone))
                    if(true)//(activity as MainActivity).myIsConnected)
                        moveUserToOtpConfirmed(view)
                    else
                        moveToUserAccountsFragment(view)

                }
        }
        else
        {
            Toast.makeText(requireContext(), "Please fill out required fields.", Toast.LENGTH_LONG).show()
        }
    }
    private fun validator (
     momoname : String ,
     email: String ,password:String
 ,momophone : String):Boolean{
        val firstCondition :Boolean = !(TextUtils.isEmpty(momoname) && TextUtils.isEmpty(password))
        val secondCondition : Boolean = !TextUtils.isEmpty(momophone)
     return !(firstCondition && secondCondition)

 }
    private fun checkifPhoneisValid(): Boolean
    {
        if (binding.registrationMomoPhone.text?.length == Constants.CHARACTER_COUNT_PHONE)
                return true
        binding.registrationMomoPhone.requestFocus()
            return false
    }
    /**
     * change Submit button text to progress bar
     *      for Feedback on button click
    * */
    private fun changeTextToProgressbar(view: View) {
            drawableSpan.startProgress()
            (view as Button).isEnabled = false
    }


    private fun moveUserToOtpConfirmed(view: View) {
        val mainLooperHandler = Handler(Looper.getMainLooper())
        mainLooperHandler.postDelayed(Runnable{
        view.findNavController().navigate(R.id.action_registerFragment_to_otpConfirmFragment,mBundle)}
        ,1500)
    }

    private fun moveToUserAccountsFragment(view: View) {
        val mainLooperHandler = Handler(Looper.getMainLooper())
        mainLooperHandler.postDelayed(Runnable {
            view.findNavController().navigate(R.id.action_registerFragment_to_userAccountsFragment)
        }, 1500)

    }


    override fun onStop() {
        super.onStop()
        drawableSpan.stopProgress()

    }
    override fun onDestroy() {
        super.onDestroy()
        drawableSpan.stopProgress()
    }
}