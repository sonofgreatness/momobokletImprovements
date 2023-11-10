package com.free.momobooklet_by_sm.domain.use_cases.manage_users

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.local.models.UserModel
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RegisterUserInFirebaseUseCase
@Inject constructor(val userRepository: UserRepository)
{

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private  lateinit var activity : Activity

    operator fun invoke(user : UserModel, activity: Activity) : Flow<Resource<String>> = flow{
        this@RegisterUserInFirebaseUseCase.activity = activity
        try {


            emit(Resource.Loading("Loading Registration"))


            val historyDeferred = CompletableDeferred<Resource<String>>()
            val phoneNumber = Constants.COUNTRY_CODE.plus(user.MoMoNumber)

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(activity) // Activity (for callback binding)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @SuppressLint("LogNotTimber")
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                        Log.d(Constants.FIREBASE_TAG, "onVerificationCompleted:$credential")
                        signInWithPhoneAuthCredential(credential)
                        historyDeferred.complete(Resource.Success("VerificationCompleted"))
                    }

                    @SuppressLint("LogNotTimber")
                    override fun onVerificationFailed(e: FirebaseException) {
                        Log.w(Constants.FIREBASE_TAG, "onVerificationFailed", e)
                        when (e) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                historyDeferred.complete(
                                    Resource.Error(
                                        "FirebaseAuthInvalidCredentialsException ".plus(
                                            e.message
                                        )
                                    )
                                )
                            }
                            is FirebaseTooManyRequestsException -> {
                                historyDeferred.complete(
                                    Resource.Error(
                                        "FirebaseTooManyRequestsException".plus(
                                            e.message
                                        )
                                    )
                                )
                            }
                            else -> {
                                historyDeferred.complete(Resource.Error("VerificationFailed".plus(e.message)))
                            }
                        }
                    }

                    @SuppressLint("LogNotTimber")
                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken,
                    ) {
                        Log.d(Constants.FIREBASE_TAG, "onCodeSent:$verificationId")
                       CoroutineScope(Dispatchers.IO).launch {
                           userRepository.updateUser(
                               attachVerificationIdToUser(
                                   verificationId,
                                   user
                               )
                           )
                       }
                            historyDeferred.complete(Resource.Success("CodeSent"))

                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

            emit(historyDeferred.await())
        }
        catch (e:Exception)
        {
            emit(Resource.Error("Failed To Initiate  PhoneAuth"))
            Timber.d("UseCaseError -> ${e.message}")
        }

    }


    /*********************************************************************************
     * attachVerificationIdToUser - Updates User  record in database by
     *                               changing the FireBaseVerificationId field
     *                               and IsRemoteRegistered
     *
     *@param verificationId: String the new value of the field
     *@param user : UserModel -> The  original user record
     *@return Returns UserModel
     *********************************************************************************/
    private fun attachVerificationIdToUser(verificationId: String, user: UserModel): UserModel {

        return UserModel(
            user.MoMoName, user.MoMoNumber,
            user.AgentEmail, user.AgentPassword, user.IsIncontrol,
            IsRemoteRegistered = true
            , verificationId
        )

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

}