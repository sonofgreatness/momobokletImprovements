package com.free.momobooklet_by_sm.presentation.manageuser

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.MainActivity2
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.FireBaseRegistrationState
import com.free.momobooklet_by_sm.databinding.FragmentOtpConfirmBinding
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpConfirmFragment2 : Fragment() {


    private lateinit var _binding: FragmentOtpConfirmBinding
    private val  binding get() = _binding
    private lateinit  var  rootView: View
    private lateinit var  mUserViewModel: UserViewModel
    private lateinit var  userPhoneNumber: String
    private  var  dialogBundle : Bundle = Bundle()
    private val positionAnim : ValueAnimator = ObjectAnimator.ofInt(this, "wordPosition", 0, 3)
    private var strings:Array<String> = arrayOf("loading   ","loading.  ", "loading.. ", "loading...")
    var position : Int = 0
    private lateinit var  view_to_animate : View
    private  var submitButtonClicked : Boolean  = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOtpConfirmBinding.inflate(inflater, container,false)
        mUserViewModel = (activity as MainActivity2).mUserViewModel


        userPhoneNumber = requireArguments().getString(Constants.PHONE_NUMBER_KEY).toString()
        binding.otpSubtitleUsermobile.text =

            "( ".plus(Constants.COUNTRY_CODE).plus(" )").plus(userPhoneNumber)

       setButtonInActiveState()
        setupUpNavigationOnClick()
        setupSubmitButtonOnClick()
        registerUser()


        rootView = binding.root
        return rootView
    }


    /****************************************
     *1 . Check Connectivity ,
     *     if connected  begin Registration
     *       collect result from Registration
     *
     ***************************************/

    @SuppressLint("LogNotTimber")
    private fun registerUser()
    {

       if(mUserViewModel.usableState.value == UserViewModel.MyState.Fetched) // internet available

        {
            mUserViewModel.registerUserWithPhoneNumber(userPhoneNumber, activity as MainActivity2)
            updateUIafterRegistration()

        }
        else
        {
            notifyUserNoConnection()
        }
    }


    /*************************************************
     * gets the status of registration process
     *
     ***********************************************/
    @SuppressLint("LogNotTimber")
    private fun updateUIafterRegistration() {

        val stateOfRegistration = mUserViewModel.registrationStateTobeUsedOnMainThread.value
        Log.d("sop2", "$stateOfRegistration")
        stateOfRegistration.apply {
            when {
                this == FireBaseRegistrationState.Loading -> {
                    waitThenHandleCodeSent()
                }
                this  == FireBaseRegistrationState.Success -> {
                    handleCodeSentCase()
                }
                else -> { //error
                    notifyUserFailedRegistration(
                        resources.getString(R.string.FireBaseRegistrationError),
                        (this as FireBaseRegistrationState.Error).exception_message?: "Registration failed , Error Unknown"
                    )
                }
            }
        }
    }


    private fun waitThenHandleCodeSent() {

        lifecycleScope.launch{
            delay(3000L)
            handleCodeSentCase()
        }
    }


    /**************************************************************************
     * Enable Submit Button,
     *                  Collect OTP,
     *                  Sign In User To FireBase
     *************************************************************************/
    private fun handleCodeSentCase() {
        stopAnimationonButton()

    }

    private fun notifyUserFailedRegistration(title: String,message: String) {
        moveToDialogueFragmentFromRegistration(title, message)
    }
    /******************************************
     * Disables button ,
     *     set backGround Color to Gray,
     *      animate button text
     *******************************************/
    private fun setButtonInActiveState() {
        binding.otpEnterBtn.apply{
            isEnabled = false
            setBackgroundColor(resources.getColor(R.color.DarkGray))

        }
        setupButtonAnimation()
    }

    /***************************************************
     * starts animating the ellipsis  on  button
     *******************************************************/
    private fun setupButtonAnimation() {

        view_to_animate = binding.otpEnterBtn

        positionAnim.duration = 1500
        positionAnim.repeatCount = ValueAnimator.INFINITE
        positionAnim.repeatMode = ValueAnimator.RESTART
        positionAnim.start()

    }
    /****************************************************************
     * stops animating ellipsis
     *      reverts backGround Tint to bluelikeTwitter
     *      enables button
     *************************************************************/
    private fun stopAnimationonButton(){
        positionAnim.cancel()
        binding.otpEnterBtn.apply {
            text = resources.getString(R.string.submit)

            val backgroundColorAnimator = ObjectAnimator.ofObject(
                this,
                "backgroundColor",
                ArgbEvaluator(),
                resources.getColor(R.color.DarkGray),
                resources.getColor(R.color.blueliketwitter)
            )
            backgroundColorAnimator.duration = 300
            backgroundColorAnimator.start()
            isEnabled = true
        }
    }

    /***************************************************************
     * Collect OTP from EDIT TEXT
     **************************************************************/
    private fun setupSubmitButtonOnClick() {
        binding.otpEnterBtn.setOnClickListener {
            if (checkOTPLength()) {
                submitButtonClicked = true
                setUpObserveactions(collectOTP())
                sigInUser(collectOTP())

            }
            else
                submitButtonClicked = false
        }
    }




    private fun checkOTPLength() : Boolean{
        return if (binding.otpEditText1.text?.length ==Constants.CHARACTER_COUNT_OTP)
            true
        else{
            binding.otpEditText1.requestFocus()
            false
        }
    }
    private fun collectOTP():String{
        return binding.otpEditText1.text.toString()
    }




    /******************************************************************************
     * when submit is  clicked move to user Accounts fragment
     *    or Error DialogueFragment
     *
     *******************************************************************************/
    private fun setUpObserveactions(otp:String) {
        mUserViewModel.signInStateTobeUsedOnMainThread.observe(viewLifecycleOwner)
        {
            if (submitButtonClicked)
            {
                when(it){

                    is FireBaseRegistrationState.Success->
                    {
                        moveToMainActivity()
                    }
                    is FireBaseRegistrationState.Error -> {

                        handleSignInError(resources.getString(R.string.FireBaseSignInError),
                            it.exception_message?: "Error Unknown",
                            otp)
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Loading",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**************************
     *
     *************************/
    private fun sigInUser(otp: String) {

        val connectivityState = mUserViewModel.usableState.value

        if(connectivityState == UserViewModel.MyState.Fetched) // internet available

        {
            mUserViewModel.signInUserIn(otp,userPhoneNumber,activity  as MainActivity2)
            updateUIafterSignIn(resources.getString(R.string.FireBaseSignInError),
                otp)
        }
        else
        {

            notifyUserNoConnection()
        }
    }

    @SuppressLint("LogNotTimber")
    private fun updateUIafterSignIn(title: String, otp: String) {
        Log.d("upD2", "updateSignInCalled")

        val stateOfSignIn = mUserViewModel.signInStateTobeUsedOnMainThread.value
        Log.d("stateOfSignIn2", "$stateOfSignIn")
        stateOfSignIn.apply {
            when {
                this == FireBaseRegistrationState.Loading -> {
                    //updateUIafterSignIn(resources.getString(R.string.FireBaseSignInError), otp)
                    Toast.makeText(requireContext(), "SignIn Loading", Toast.LENGTH_SHORT).show()
                }
                this  == FireBaseRegistrationState.Success -> {
                    handleSignInSuccess()
                }
                else -> { //error
                    handleSignInError(title,
                        (this as  FireBaseRegistrationState.Error).exception_message?: "Error Unknown",
                        otp)
                }
            }
        }

    }


    /***************************************************************************************
     * Alerts User Of Successful Sign In
     **************************************************************************************/
    private fun handleSignInSuccess() {
        Toast.makeText(requireContext(), "Successful Sign In", Toast.LENGTH_SHORT).show()
        moveToMainActivity()
    }

    /****************************************************************************************
     * calls moveToDialogueFragmentFromSignIn
     ***************************************************************************************/
    private fun handleSignInError(errorTitle: String, errorMessage: String, otp: String) {
        moveToDialogueFragmentFromSignIn(errorTitle, errorMessage,otp)
    }


    private fun notifyUserNoConnection() {
        val title = resources.getString(R.string.FireBaseConnectivityError)
        val message= resources.getString(R.string.FireBaseConnectivityErrorMessageBody)
        moveToDialogueFragmentFromRegistration(title,message)
    }


    /**********************************************************************************************
     *shows Alert  Dialogue
     * @param errorTitle : String title Of error  passed to DialogFragment
     *                       as navigation argument
     *@param errorMessage :String  passed to  DialogFragment
     *                  as navigation argument
     ***********************************************************************************************/
    private  fun  moveToDialogueFragmentFromRegistration(errorTitle: String, errorMessage: String)

    {
        AlertDialog.Builder(requireContext())
            .setTitle(errorTitle)
            .setMessage(errorMessage)
            .setPositiveButton("OK"){ _,_ ->
                moveToMainActivity()
            }.show()

    }

    /**********************************************************************************************
     * shows Alert  Dialogue
     * @param errorTitle : String title Of error  passed to DialogFragment
     *                       as navigation argument
     *@param errorMessage :String  passed to  DialogFragment
     *                  as navigation argument
     ***********************************************************************************************/
    private  fun  moveToDialogueFragmentFromSignIn(errorTitle: String, errorMessage: String, otp:String)

    {
        AlertDialog.Builder(requireContext())
            .setTitle(errorTitle.plus("   OTP -> ").plus(otp))
            .setMessage(errorMessage)
            .setPositiveButton("OK"){ _,_ ->
                moveToMainActivity()
            }.show()
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
            findNavController().navigate(R.id.action_otpConfirmFragment2_to_registerFragment2)
        }
}



    fun setWordPosition(position : Int){
        this.position  = position
        (view_to_animate as TextView).text = strings[position]
    }
    fun getWordPosition() : Int{
        return(position)
    }
    override fun onStop() {
        positionAnim.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        positionAnim.cancel()
        super.onDestroy()
    }

}