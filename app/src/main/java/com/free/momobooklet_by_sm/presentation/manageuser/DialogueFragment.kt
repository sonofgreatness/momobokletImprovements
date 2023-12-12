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
import com.free.momobooklet_by_sm.common.util.Constants.Companion.BACKEND_REG_FLAG
import com.free.momobooklet_by_sm.common.util.classes.Role
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
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
       userPhoneNumber  = requireArguments().getString(Constants.PHONE_NUMBER_KEY).toString()

            val userMoMoName =requireArguments().getString(Constants.MOMO_NAME_KEY).toString()
            val userEmail =requireArguments().getString(Constants.MOMO_EMAIL_KEY).toString()
            val userPassword = requireArguments().getString(Constants.MOMO_PASSWORD_KEY).toString()
             val mBundle = Bundle()

            mUserViewModel = (activity as MainActivity).mUserViewModel

           return  AlertDialog.Builder(requireContext())
           .setTitle(title)
           .setMessage(message)
           .setPositiveButton(getString(R.string.retry)) { _, _ ->

               if(mUserViewModel.usableState.value == UserViewModel.MyState.Fetched) // internet available
               {

                   mBundle.putString(Constants.PHONE_NUMBER_KEY, userPhoneNumber)
                   mBundle.putString(Constants.MOMO_NAME_KEY, userMoMoName)
                   mBundle.putString(Constants.MOMO_PASSWORD_KEY, userPassword)
                   mBundle.putString(Constants.MOMO_EMAIL_KEY, userEmail)

                   findNavController().navigate(R.id.action_dialogueFragment_to_otpConfirmFragment,mBundle)

               }
               else{
                   moveToUserAccountsFragment()
               }

           }
           .setNegativeButton(getString(R.string.cancel)) { _, _ ->
               moveToUserAccountsFragment()
           }
           .create()
   }




    override fun onCancel(dialog: DialogInterface) {
      moveToUserAccountsFragment()
      super.onCancel(dialog)
   }

    private fun moveToUserAccountsFragment() {
        findNavController().navigate(R.id.action_dialogueFragment_to_userAccountsFragment)
    }
}
