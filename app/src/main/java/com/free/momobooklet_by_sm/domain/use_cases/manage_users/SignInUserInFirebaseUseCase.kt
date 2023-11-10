package com.free.momobooklet_by_sm.domain.use_cases.manage_users

import android.app.Activity
import android.util.Log
import com.free.momobooklet_by_sm.common.util.classes.Exceptions.IncorrectSmsCodeLengthException
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.local.models.UserModel
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/***********************************************************************************
 * Handles Sign In to Firebase  creates phone auth credential using
 *       verificationId from UserModel    and code (SMS)
 *
 ************************************************************************************/
class SignInUserInFirebaseUseCase @Inject constructor(val userRepository: UserRepository)
{
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val historyDeferred = CompletableDeferred<Resource<String>>()
    private  lateinit var activity : Activity

    operator fun invoke(user : UserModel, smsCode:String, activity: Activity) : Flow<Resource<String>> = flow{
        this@SignInUserInFirebaseUseCase.activity = activity
        emit(Resource.Loading("Loading Signing "))
        try {

            checksmsCodeLength(smsCode)
            val credential = PhoneAuthProvider.getCredential(user.FireBaseVerificationId!!, smsCode)
            signInWithPhoneAuthCredential(credential)
            emit(historyDeferred.await())
        }
        catch (ex: IncorrectSmsCodeLengthException)
        {
            emit(Resource.Error("Failed To Sign In OTP supplied is the Wrong Length(!= 6)"))
        }
        catch (ex : Exception)
        {
            emit(Resource.Error("Failed To Sign In  due to exception : ${ex.message}"))
        }

    }

    private fun checksmsCodeLength(smsCode: String){
        if (smsCode.length != 6)
            throw IncorrectSmsCodeLengthException("OTP supplied is of length ${smsCode.length} < 6")
    }

    /*********************************************************************
     *
     ***********************************************************************/
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG 4", "signInWithCredential:success")
                    historyDeferred.complete(Resource.Success("Sign In Complete"))
                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        historyDeferred.complete(Resource.Error("The verification code entered was invalid"))
                    }
                    historyDeferred.complete(Resource.Error("Sign In failed due to  exception : ${task.exception?.message}"))
                }
            }
    }
}




