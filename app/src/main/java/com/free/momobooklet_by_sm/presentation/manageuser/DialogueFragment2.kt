package com.free.momobooklet_by_sm.presentation.manageuser

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.MainActivity2
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel


/********************************************************************************************
 * A simple [Dialogue Fragment] .
 *  Handles Firebase Registration And
 *            Log In Errors,
 * Displays Error Message  to User,
 * Collects User Response :
 *     on positive Response -> restarts  failed process
 *     on negative Response ->  move to UserAccounts Fragment
 **********************************************************************************************/
class DialogueFragment2 : DialogFragment() {

    private lateinit var  title : String
    private lateinit var  message : String
    private  lateinit var  mUserViewModel: UserViewModel
    private  lateinit  var userPhoneNumber: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        title = requireArguments().getString(Constants.FIREBASE_REGISTRATION_KEY).toString()
        message =   requireArguments().getString(Constants.FIREBASE_REGISTRATION_ERROR_MESSAGE_KEY).toString()
        mUserViewModel = (activity as MainActivity2).mUserViewModel

        Toast.makeText((activity as MainActivity2).applicationContext, "Failed to  re register", Toast.LENGTH_SHORT).show()


        userPhoneNumber  = requireArguments().getString(Constants.PHONE_NUMBER_KEY).toString()

        val userMoMoName =requireArguments().getString(Constants.MOMO_NAME_KEY).toString()
        val userEmail =requireArguments().getString(Constants.MOMO_EMAIL_KEY).toString()
        val userPassword = requireArguments().getString(Constants.MOMO_PASSWORD_KEY).toString()
        val mBundle = Bundle()

        return  AlertDialog.Builder((activity as MainActivity2).applicationContext)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->

                if(mUserViewModel.usableState.value == UserViewModel.MyState.Fetched) // internet available
                {

                    mBundle.putString(Constants.PHONE_NUMBER_KEY, userPhoneNumber)
                    mBundle.putString(Constants.MOMO_NAME_KEY, userMoMoName)
                    mBundle.putString(Constants.MOMO_PASSWORD_KEY, userPassword)
                    mBundle.putString(Constants.MOMO_EMAIL_KEY, userEmail)

                    findNavController().navigate(R.id.action_dialogueFragment2_to_otpConfirmFragment2,mBundle)

                }
                else{
                    moveToMainActivity()
                }

            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                moveToMainActivity()
            }
            .create()
    }


    override fun onCancel(dialog: DialogInterface) {
        moveToMainActivity()
        super.onCancel(dialog)
    }

    private fun moveToMainActivity() {
        val i = Intent(requireContext(), MainActivity::class.java)
        startActivity(i)
    }
}