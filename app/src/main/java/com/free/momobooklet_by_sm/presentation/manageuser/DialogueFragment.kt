package com.free.momobooklet_by_sm.presentation.manageuser

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.free.momobooklet_by_sm.MainActivity
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
 class DialogueFragment : DialogFragment() {

    private lateinit var  title : String
    private lateinit var  message : String
    private  lateinit var  mUserViewModel: UserViewModel
    private  lateinit  var userPhoneNumber: String

   override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

       title = requireArguments().getString(Constants.FIREBASE_REGISTRATION_KEY).toString()
       message =   requireArguments().getString(Constants.FIREBASE_REGISTRATION_ERROR_MESSAGE_KEY).toString()
       userPhoneNumber  = requireArguments().getString(Constants.FIREBASE_REGISTRATION_ERROR_MESSAGE_KEY).toString()
       val otp = requireArguments().getString(Constants.OTP_KEY).toString()
       mUserViewModel = (activity as MainActivity).mUserViewModel

           return  AlertDialog.Builder(requireContext())
           .setTitle(title)
           .setMessage(message)
           .setPositiveButton(getString(R.string.retry)) { _, _ ->
               if (otp.isEmpty())
               restartRegistration()
               else
                   restartSignIn(otp)

           }
           .setNegativeButton(getString(R.string.cancel)) { _, _ ->
               moveToUserAccountsFragment()
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
      moveToUserAccountsFragment()
      super.onCancel(dialog)
   }

    private fun moveToUserAccountsFragment() {
        findNavController().navigate(R.id.action_dialogueFragment_to_userAccountsFragment)
    }
}
