package com.free.momobooklet_by_sm.presentation.manageuser

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.Constants.Companion.BACKEND_REG_FLAG
import com.free.momobooklet_by_sm.common.util.classes.Role
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.BackEndRegistrationState
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.FireBaseRegistrationState
import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
import com.free.momobooklet_by_sm.databinding.FragmentOtpConfirmBinding
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class OtpConfirmFragment  : Fragment() {



    private lateinit var _binding: FragmentOtpConfirmBinding
    private val  binding get() = _binding
    private lateinit  var  rootView: View
    private lateinit var  mUserViewModel: UserViewModel
    private lateinit var  userPhoneNumber: String
    private lateinit var userMoMoName : String
    private lateinit var userPassword : String
    private lateinit var userEmail:String
    private  var  dialogBundle : Bundle = Bundle()
    private val positionAnim : ValueAnimator = ObjectAnimator.ofInt(this, "wordPosition", 0, 3)
    private var strings:Array<String> = arrayOf("loading   ","loading.  ", "loading.. ", "loading...")
    var position : Int = 0
    private lateinit var  view_to_animate : View

    private  var submitButtonClicked : Boolean  = false




    @SuppressLint("LogNotTimber")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOtpConfirmBinding.inflate(inflater, container,false)
        mUserViewModel = (activity as MainActivity).mUserViewModel
        userMoMoName =requireArguments().getString(Constants.MOMO_NAME_KEY).toString()
        userEmail =requireArguments().getString(Constants.MOMO_EMAIL_KEY).toString()
        userPassword = requireArguments().getString(Constants.MOMO_PASSWORD_KEY).toString()

        userPhoneNumber = requireArguments().getString(Constants.PHONE_NUMBER_KEY).toString()

        binding.otpSubtitleUsermobile.text =

            "( ".plus(Constants.COUNTRY_CODE).plus(" )").plus(userPhoneNumber)

        setButtonInActiveState()
        setupUpNavigationOnClick()
        setupSubmitButtonOnClick()
        //registerUser()
        authenticateUser()


        rootView = binding.root
        return rootView
    }

    private fun authenticateUser() {
        if(mUserViewModel.usableState.value == UserViewModel.MyState.Fetched) // internet available

        {

            //mUserViewModel.registerUserWithPhoneNumber(userPhoneNumber, activity as MainActivity)
            val registrationRequest = AuthenticationRequest(
                "71000000",
                  "pool"
            )
                //getRegistrationRequest()



            val authenticationRequest = getAuthenticationRequest()
            mUserViewModel.authenticateUserInBackEnd(authenticationRequest, requireActivity())
            //updateUIafterRegistrationInFireBase()
            updateUIafterRegistrationInBackend()



        }
        else
        {
            notifyUserNoConnection()
            Log.d("upL1", "noNetworkFromRegister")
        }
    }

    private fun getAuthenticationRequest(): AuthenticationRequest {


        return  AuthenticationRequest(
            username = userPhoneNumber,
            password =  userPassword
        )
    }


    /***************************************************************
     * Collect OTP from EDIT TEXT
     *
     *
     * gets the status of FireBase DB registration process
     *                when status  is Success or  Loading
     *                  enables button
     *                when status is Error
     **************************************************************/
    private fun setupSubmitButtonOnClick() {

        val stateOfRegistration = mUserViewModel.registrationStateTobeUsedOnMainThread.value
        Timber.d("submitOnClick stateOf FirebaseRegistration  -->  $stateOfRegistration")
        binding.otpEnterBtn.setOnClickListener {
            if (checkOTPLength()) {
                processFireBaseSignInState(stateOfRegistration)
                submitButtonClicked = true
            }
            else
                submitButtonClicked = false
        }
    }

    /**
     * processFireBaseSignInState
     *  -- reads firebase Registration state
     *     -- begins sign in if state is +
     *     --
     **/
    private fun processFireBaseSignInState(stateOfRegistration: FireBaseRegistrationState?) {

        when(stateOfRegistration)
        {
            is FireBaseRegistrationState.Success ->{
                setUpObserveactions(collectOTP())
                sigInUser(collectOTP())
            }
            else ->
            {
                 Toast.makeText(requireContext(), "moved from processFirebaseSignInState", Toast.LENGTH_SHORT).show()
                // moveToUserAccountsFragment()
                setUpObserveactions(collectOTP())
                sigInUser(collectOTP())

            }
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
                        moveToUserAccountsFragment()
                        Toast.makeText(requireContext(), "moved from setupObservers", Toast.LENGTH_SHORT).show()
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
            mUserViewModel.signInUserIn(otp,userPhoneNumber,activity  as MainActivity)
            updateUIafterSignIn(resources.getString(R.string.FireBaseSignInError),
                otp)
        }
        else
        {

            notifyUserNoConnection()
        }
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

            //mUserViewModel.registerUserWithPhoneNumber(userPhoneNumber, activity as MainActivity)
            val registrationRequest = getRegistrationRequest()
            mUserViewModel.registerUserInBackEndDB(registrationRequest, requireActivity())
            //updateUIafterRegistrationInFireBase()
            updateUIafterRegistrationInBackend()



        }
        else
        {
            notifyUserNoConnection()
            Log.d("upL1", "noNetworkFromRegister")
        }
    }



    private fun getRegistrationRequest(): UserRegistrationRequest {
        return UserRegistrationRequest(
            userPhoneNumber,
            userMoMoName,
            userPassword,
            userEmail,
            Role.USER
        )
    }

        /*************************************************
         *
         * gets the status of BackEnd DB registration process
         *                when status  is Success or  Loading
         *                  enables button
         *                when status is Error
         ***********************************************/
        private fun updateUIafterRegistrationInBackend() {
            val stateOfRegistration = mUserViewModel.backendregistrationStateTobeUsedOnMainThread.value
            Timber.d("back end registration process status---..---> $stateOfRegistration")
            stateOfRegistration.apply {
                when {
                    this == BackEndRegistrationState.Loading -> {
                        Toast.makeText(requireContext(), "Back end registration state    -> LOADING",Toast.LENGTH_SHORT).show()
                       // waitThenHandleCodeSent()
                    }
                    this  == BackEndRegistrationState.Success -> {
                        Toast.makeText(requireContext(), "Back end registration state    -> SUCCESS",Toast.LENGTH_SHORT).show()
                         handleCodeSentCase()
                    }

                    else -> { //error
                        if (this != null)
                        {
                            notifyUserFailedBackEndRegistration(
                                resources.getString(R.string.FireBaseRegistrationError),
                                (this as BackEndRegistrationState.Error).exception_message?: "Registration failed , Error Unknown"
                            )
                        }
                        else
                        {
                            Toast.makeText(requireContext(), "welcome back \uD83D\uDE40 ",Toast.LENGTH_SHORT).show()
                             waitThenHandleCodeSent()
                        }
                    }
                }
            }
        }
    /*************************************************
     * gets the status of registration process
     *
     ***********************************************/
    @SuppressLint("LogNotTimber")
    private fun updateUIafterRegistration() {

        val stateOfRegistration = mUserViewModel.registrationStateTobeUsedOnMainThread.value
        Log.d("sop", "$stateOfRegistration")
        stateOfRegistration.apply {
            if (this == FireBaseRegistrationState.Loading) {
                waitThenHandleCodeSent()
            } else if (this  == FireBaseRegistrationState.Success) {
                handleCodeSentCase()
            } else { //error
                notifyUserFailedRegistration(
                    resources.getString(R.string.FireBaseRegistrationError),
                    (this as FireBaseRegistrationState.Error).exception_message?: "Registration failed , Error Unknown"
                )
            }
        }
    }


    private fun waitThenHandleCodeSent() {

        lifecycleScope.launch{
            delay(3000L)
            handleCodeSentCase()
        }
    }


    private fun notifyUserFailedBackEndRegistration(title: String,message: String) {
        moveToDialogueFragmentFromBackEndRegistration(title, message)
    }

    /**********************************************************************************************
     *navigates to DialogFragment
     * @param errorTitle : String title Of error  passed to DialogFragment
     *                       as navigation argument
     *@param errorMessage :String  passed to  DialogFragment
     *                  as navigation argument
     ***********************************************************************************************/
    private  fun  moveToDialogueFragmentFromBackEndRegistration(errorTitle: String, errorMessage: String)

    {
        dialogBundle.putString(Constants.FIREBASE_REGISTRATION_KEY,errorTitle)
        dialogBundle.putString(Constants.FIREBASE_REGISTRATION_ERROR_MESSAGE_KEY, errorMessage)
        dialogBundle.putString(Constants.PHONE_NUMBER_KEY, userPhoneNumber)


        dialogBundle.putBoolean(BACKEND_REG_FLAG,true)
        dialogBundle.putString(Constants.MOMO_NAME_KEY, userMoMoName)
        dialogBundle.putString(Constants.MOMO_PASSWORD_KEY, userPassword )
        dialogBundle.putString(Constants.MOMO_EMAIL_KEY, userEmail )

        findNavController().navigate(R.id.action_otpConfirmFragment_to_dialogueFragment,dialogBundle)
    }

    private fun notifyUserNoConnection() {
        val title = resources.getString(R.string.FireBaseConnectivityError)
        val message= resources.getString(R.string.FireBaseConnectivityErrorMessageBody)
        moveToDialogueFragmentFromBackEndRegistration(title,message)
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
        moveToDialogueFragmentFromFireBaseRegistration(title, message)
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


    @SuppressLint("LogNotTimber")
    private fun updateUIafterSignIn(title: String, otp: String) {
        Log.d("upD", "updateSignInCalled")

        val stateOfSignIn = mUserViewModel.signInStateTobeUsedOnMainThread.value
        Log.d("stateOfSignIn", "$stateOfSignIn")
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
        moveToUserAccountsFragment()
    }


    /****************************************************************************************
     * calls moveToDialogueFragmentFromSignIn
     ***************************************************************************************/
    private fun handleSignInError(errorTitle: String, errorMessage: String, otp: String) {
        moveToDialogueFragmentFromSignIn(errorTitle, errorMessage,otp)
    }


    /*************************************************************
     *
     *************************************************************/
    private fun moveToUserAccountsFragment() {
        findNavController().navigate(R.id.action_otpConfirmFragment_to_userAccountsFragment)
    }




    /**********************************************************************************************
     *navigates to DialogFragment
     * @param errorTitle : String title Of error  passed to DialogFragment
     *                       as navigation argument
     *@param errorMessage :String  passed to  DialogFragment
     *                  as navigation argument
     ***********************************************************************************************/
    private  fun  moveToDialogueFragmentFromFireBaseRegistration(errorTitle: String, errorMessage: String)

    {
        dialogBundle.putString(Constants.FIREBASE_REGISTRATION_KEY,errorTitle)
        dialogBundle.putString(Constants.FIREBASE_REGISTRATION_ERROR_MESSAGE_KEY, errorMessage)
        dialogBundle.putString(Constants.PHONE_NUMBER_KEY, userPhoneNumber)

        findNavController().navigate(R.id.action_otpConfirmFragment_to_dialogueFragment,dialogBundle)
    }

    /**********************************************************************************************
     *navigates to DialogFragment
     * @param errorTitle : String title Of error  passed to DialogFragment
     *                       as navigation argument
     *@param errorMessage :String  passed to  DialogFragment
     *                  as navigation argument
     ***********************************************************************************************/
    private  fun  moveToDialogueFragmentFromSignIn(errorTitle: String, errorMessage: String, otp:String)

    {
        dialogBundle.putString(Constants.FIREBASE_REGISTRATION_KEY,errorTitle)
        dialogBundle.putString(Constants.FIREBASE_REGISTRATION_ERROR_MESSAGE_KEY, errorMessage)
        dialogBundle.putString(Constants.PHONE_NUMBER_KEY, userPhoneNumber)
        dialogBundle.putString(Constants.OTP_KEY, otp)

        findNavController().navigate(R.id.action_otpConfirmFragment_to_dialogueFragment,dialogBundle)
    }

    private fun setupUpNavigationOnClick() {
        val mBundle = Bundle()
        mBundle.putString(Constants.REGISTRATION_HOME_KEY, "otpconfirm_fragment")
        binding.appBarOtp.setOnClickListener {
            it.findNavController().navigate(R.id.action_otpConfirmFragment_to_registerFragment,mBundle)
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