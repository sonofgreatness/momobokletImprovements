package com.example.momobooklet_by_sm.util.classes

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

/**
 *This class is responsible for creating new Users
 *It adds a UserModel object to RoomDB entinty : UserAccounts
 *It delegates the addition of a UserModel Object to the **RightClass
 *@param mUserViewModel : to Access roomdatabase
 */
class AddUserAcc(val phoneNum : String,
                 val activity: Activity,val auth: FirebaseAuth)
{

    val phoneNumber =phoneNum
    private var smsString: String = "00000"
    private var   storedVerificationId: String? = "default"
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private  var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    init {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d("TAG2", "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
                // Show a message and update the UI
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG7", "onCodeSent:$verificationId")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        beginAddAccount()
    }
    /*
    * ALWAYS CALL setSMS before this Function
    **/
     fun signInwithSms() {
        try {
            val credential = PhoneAuthProvider.getCredential(
                storedVerificationId!!,smsString)
            Log.d("credentialfromsms:","$smsString")
            signInWithPhoneAuthCredential(credential)
        } catch (ex: Exception) {
            Log.d("exceptionnfromsms:","${ex.message}")
        }
    }

    fun setSMS(string: String)
    {
        smsString = string
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG 4", "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun   beginAddAccount() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}