package com.free.momobooklet_by_sm.presentation.help

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.free.momobooklet_by_sm.BackUpActivity
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.classes.RecoveryActionType
import com.free.momobooklet_by_sm.databinding.FragmentFirstBinding
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.BackupDataBaseViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var mBackUpDatabaseViewModel: BackupDataBaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        mBackUpDatabaseViewModel= (activity as BackUpActivity).mBackUpDatabaseViewModel
        setUpImageOnClicks()
        setUpImageAnimations()
        return binding.root

    }
    private fun setUpImageOnClicks() {
        setUpRecoverImageOnClick()
        setUpBackUpImageOnClick()
        setUpDefaultRecoverButtonOnClick()
        setUpCustomRecoverButtonOnClick()
        setUpBackupButtonOnClick()
    }

    private fun setUpRecoverImageOnClick() {
        binding.backupRecoverRL.setOnClickListener {
            binding.recoverButtonsLL.visibility = View.VISIBLE
            binding.backupButtonsLL.visibility = View.GONE
            binding.backupBtnDefault.isEnabled = false
            binding.recoverBtnCustom.isEnabled = true
            binding.recoverBtnDefault.isEnabled = true
        }
    }

    private fun setUpBackUpImageOnClick() {
        binding.backupRL.setOnClickListener {
            binding.recoverButtonsLL.visibility = View.GONE
            binding.backupButtonsLL.visibility = View.VISIBLE
            binding.backupBtnDefault.isEnabled = true
            binding.recoverBtnCustom.isEnabled = false
            binding.recoverBtnDefault.isEnabled = false
        }
    }

    private fun setUpDefaultRecoverButtonOnClick(){
        binding.recoverBtnDefault.setOnClickListener{
            showWarningMessage(RecoveryActionType.DEFAULT_RECOVER)
        }

    }


    private fun setUpCustomRecoverButtonOnClick(){
        binding.recoverBtnCustom.setOnClickListener{
            showWarningMessage(RecoveryActionType.CUSTOM_RECOVER)
        }

    }




    private fun setUpBackupButtonOnClick(){
        binding.backupBtnDefault.setOnClickListener{
            Toast.makeText(requireContext(), "Back up", Toast.LENGTH_SHORT).show()
            showWarningMessage(RecoveryActionType.BACKUP)
            backupDatabase()
        }
    }
    private fun showWarningMessage(action: RecoveryActionType) {
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.recovery_warning))
            .setIcon(resources.getDrawable(R.drawable.report_error))
            .setMessage(resources.getString(R.string.recovery_warning_body))
            .setPositiveButton("OK") { _, _ ->
                when (action){
                     RecoveryActionType.BACKUP ->{
                              backupDatabase()
                     }
                    RecoveryActionType.DEFAULT_RECOVER->{
                        recoverDatabaseDefaultOption()
                    }
                    RecoveryActionType.CUSTOM_RECOVER->{
                        recoverDatabaseCustomOption()
                    }
                }
            }.show()
    }


    /*************************************************************************
     * Looks for backup file in external Storage location "MOMOBooklet"
     *       if no matching file is found alert user
     *                 to provide different file location in which to look
     *                  or abort whole process
     *
     *----------------------------------------------------------------------
     *
     * if file is found increment database version
     *         (copy database file to  location specified in MIGRATION PATH)
     ************************************************************************/
    private  fun recoverDatabaseDefaultOption(){
                mBackUpDatabaseViewModel.fullrecoverDefault(requireActivity().application)

    }
    /**************************************************************
     *
     *
     *************************************************************/
    private  fun recoverDatabaseCustomOption(){
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data // The URI with the location of the file
            if (selectedFile!= null)
                mBackUpDatabaseViewModel.fullrecoverCustom(requireActivity().application,selectedFile)
            else
                Toast.makeText(requireContext(), "Selected File Empty", Toast.LENGTH_SHORT).show()

        }
    }

    private  fun backupDatabase(){

            mBackUpDatabaseViewModel.
                            backUpApplicationData(requireActivity().application,requireActivity())
    }

    /************************************************************
     * setUpCardAnimations -> scales cards so they
     *                          provide user clue that cards
     *                          are clickable
     ************************************************************/
    private fun setUpImageAnimations() {

        //Animate card
        AnimatorInflater.loadAnimator(requireContext(), R.animator.property_animator)
            .apply {
                setTarget(binding.backupRecoverImg)
                start()
            } as AnimatorSet
        AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.property_animator_4_secs_later
        )
            .apply {
                setTarget(binding.backupImg)
                start()
            } as AnimatorSet
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}