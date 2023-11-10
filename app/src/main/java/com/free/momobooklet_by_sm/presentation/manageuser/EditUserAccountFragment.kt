package com.free.momobooklet_by_sm.presentation.manageuser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.local.models.UserModel
import com.free.momobooklet_by_sm.databinding.FragmentEditUserAccountBinding
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel


/******************************************************************************
 * A simple [Fragment] subclass.
 * Recieves User PhoneNumber (String) as a navigation argument
 *    allows user  to edit UserModel in db with same PhoneNumber
 *****************************************************************************/
class EditUserAccountFragment : Fragment() {



    private lateinit var _binding: FragmentEditUserAccountBinding
    private val  binding get() = _binding
    private lateinit  var  rootView: View
    private  lateinit var  mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditUserAccountBinding.inflate(inflater,container, false)
        rootView = binding.root
        mUserViewModel = (activity as MainActivity).mUserViewModel

        setNestedScrollViewHeight()
        setUpOldValues(reconstructUserModelFromArgs())
        setUpOnClicks(reconstructUserModelFromArgs())

        return rootView
    }

    private fun setUpOnClicks(user: UserModel) {
            setUpNavOnClick()
           setUpEditNameOnClick()
        setUpFinishOnClick(user)
    }

    private fun setUpNavOnClick() {
        binding.customUpicon.setOnClickListener{

            moveToUserAccountsFragment()
        }
    }


    private fun setUpEditNameOnClick() {
        val ediText: EditText = binding.updateNameCard.editUserDetailUpdateEditText
        val textView: TextView = binding.updateNameCard.editUserDetailNewValueValue
        binding.updateNameCard.
        editUserDetailButton.setOnClickListener {
            if (
                !checkNameDataValidity(ediText)
            )
                ediText.requestFocus()
            else {

                textView.text = ediText.text.toString().trim()
                textView.alpha = 1F
            }
        }
    }


    /****************************************************************
     * Checks if data in EditText box is valid                      *
     ****************************************************************/
    private fun checkNameDataValidity(ediText: EditText): Boolean {
        return !(ediText.text.isNullOrEmpty())
    }


    private fun setUpOldValues(user: UserModel) {

        binding.updateNameCard.apply{
            this.editUserDetailOldValueValue.text = user.MoMoName
        }


            binding.updateStatusCard.apply{
                if(user.IsIncontrol)
                    this.activeRadioBtn.isChecked = true
                else
                   this.inactiveRadioBtn.isChecked = true

            }
    }

    private fun setUpFinishOnClick(user: UserModel) {
        binding.updateStatusCard.editUserAccountButton.setOnClickListener {
            val updatedUser = collectUserInputs(user)
            mUserViewModel.updateUser(updatedUser)
            Toast.makeText(requireContext(), "Account updated", Toast.LENGTH_SHORT).show()
            moveToUserAccountsFragment()

        }
    }


    private fun moveToUserAccountsFragment()
    {
        findNavController().navigate(R.id.action_editUserAccountFragment_to_userAccountsFragment)
    }
    private fun collectUserInputs(user: UserModel):UserModel {
        val nameTextView  = binding.updateNameCard.editUserDetailNewValueValue
        val controlStatusView = binding.updateStatusCard.activeRadioBtn

        var newName : String = if (nameTextView.alpha == 1F)
            nameTextView.text.toString().trim()
        else
            user.MoMoName



        return  UserModel(newName,user.MoMoNumber, user.AgentEmail, user.AgentPassword,
                          IsIncontrol = controlStatusView.isChecked , user.IsRemoteRegistered,
                           user.FireBaseVerificationId)

    }


    /***************************************************************
     * Ensures scrollview contents never overlap
     *            to cover the view above
     ***************************************************************/
    private fun setNestedScrollViewHeight() {
        val h =  binding.root.height - binding.editAccountTitle.height
        binding.editUserAccountNestedScroll.layoutParams.height = h
    }

    private  fun reconstructUserModelFromArgs() : UserModel
    {
        return UserModel(
            requireArguments().getString(Constants.MOMO_NAME_KEY).toString().trim(),
            requireArguments().getString(Constants.PHONE_NUMBER_KEY).toString().trim(),
            requireArguments().getString(Constants.MOMO_EMAIL_KEY).toString().trim(),
            requireArguments().getString(Constants.MOMO_PASSWORD_KEY).toString().trim(),
            requireArguments().getBoolean(Constants.MOMO_CONTROL_STATUS_KEY),
            requireArguments().getBoolean(Constants.MOMO_REGISTRATION_STATUS_KEY),
            requireArguments().getString(Constants.MOMO_FIREBASEVERIFICATION_KEY)

        )


    }

}