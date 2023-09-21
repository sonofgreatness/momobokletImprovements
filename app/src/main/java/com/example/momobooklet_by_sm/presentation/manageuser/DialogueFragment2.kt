package com.example.momobooklet_by_sm.presentation.manageuser

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.MainActivity2
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.Constants
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar


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
        userPhoneNumber  = requireArguments().getString(Constants.FIREBASE_REGISTRATION_ERROR_MESSAGE_KEY).toString()
        val otp = requireArguments().getString(Constants.OTP_KEY).toString()
        mUserViewModel = (activity as MainActivity2).mUserViewModel

        Toast.makeText((activity as MainActivity2).applicationContext, "Failed to  re register", Toast.LENGTH_SHORT).show()


        return  AlertDialog.Builder((activity as MainActivity2).applicationContext)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                if (otp.isEmpty())
                    restartRegistration()
                else
                    restartSignIn(otp)

            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                moveToMainActivity()
            }
            .create()
    }

    private fun restartRegistration() {
        try {
            mUserViewModel.registerUserWithPhoneNumber(
                userPhoneNumber,
                activity as MainActivity
            )
        } catch (ex: Exception) {
            Toast.makeText(requireContext(), "Failed to  re register", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun restartSignIn(otp:String)
    {
        try {

            mUserViewModel.signInUserIn(otp,
                userPhoneNumber,
                activity as MainActivity
            )
        } catch (ex: Exception) {
            Toast.makeText(requireContext(), "Failed to  re SignIn {$userPhoneNumber , $otp}", Toast.LENGTH_SHORT).show()
            dismiss()
        }
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
