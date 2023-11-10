package com.free.momobooklet_by_sm.presentation.ui.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.BackUpState
import com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BackupDataBaseViewModel
@Inject constructor(private  val backUpdataUseCase: BackUpDatabaseUseCase,
                    private val unmountBackUpUseCase: UnmountBackUpUseCase,
                    private val recoverDatabaseDefaultUseCase: RecoverDatabaseDefaultUseCase,
                    private val unpackRecoveryUseCase: UnpackRecoveryUseCase,
                    private val recoverDatabaseCustomUseCase: RecoverDatabaseCustomUseCase
                    )

    : ViewModel()
{

    /*************************************************************************
     * creates zip file in application cache  with
     *                 all important backup data
     *
     * @param date  string to identify  the date in which the back up
     *                 file is created
     *********************************************************************/
    fun backUpApplicationData( application: Application, activity: Activity)
    {
        viewModelScope.launch {
             backUpdataUseCase().collect{
                 when(it)
                 {
                     is BackUpState.Success ->{
                         // move backupFile to external storage
                         Toast.makeText(application.applicationContext,
                                         Constants.BACKUP_SUCCESS_MESSAGE,Toast.LENGTH_SHORT).show()
                         delay(1000L)
                         unMountToExternal(application,activity)
                     }
                     is BackUpState.Loading -> {
                         Toast.makeText(application.applicationContext,
                             Constants.BACKUP_LOADING_MESSAGE,Toast.LENGTH_SHORT).show()

                     }
                     is BackUpState.Error -> {
                         Toast.makeText(application.applicationContext,
                             Constants.BACKUP_ERROR_MESSAGE.plus(it.exception_message),
                             Toast.LENGTH_SHORT).show()
                     }
                 }
             }
        }
    }
   private fun unMountToExternal(application: Application, activity: Activity){
        viewModelScope.launch {
            unmountBackUpUseCase().collect{
                when(it)
                {
                    is BackUpState.Success ->{
                        Toast.makeText(application.applicationContext,
                            Constants.EXTERRNALBACKUP_SUCCESS_MESSAGE,Toast.LENGTH_SHORT).show()
                    }
                    is BackUpState.Loading -> {



                        Toast.makeText(application.applicationContext,
                            Constants.BACKUP_LOADING_MESSAGE,Toast.LENGTH_SHORT).show()
                        delay(3000L)
                        restart(activity)

                    }
                    is BackUpState.Error -> {
                        Toast.makeText(application.applicationContext,
                            Constants.EXTERRNALBACKUP_ERROR_MESSAGE.plus(it.exception_message),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun fullrecoverDefault(application: Application)
    {
        viewModelScope.launch {
            recoverDatabaseDefaultOption(application)
            delay(1500L)
            restoreDataFromBackUp(application)
        }
    }

   private suspend fun recoverDatabaseDefaultOption(application: Application){
        viewModelScope.launch {
            recoverDatabaseDefaultUseCase().collect{
                when(it)
                {
                    is BackUpState.Success ->{
                        Toast.makeText(application.applicationContext,
                            Constants.RECOVERY_SUCCESS_MESSAGE,Toast.LENGTH_SHORT).show()

                    }
                    is BackUpState.Loading -> {
                        Toast.makeText(application.applicationContext,
                            Constants.RECOVERY_LOADING_MESSAGE,Toast.LENGTH_SHORT).show()
                    }
                    is BackUpState.Error -> {
                        Toast.makeText(application.applicationContext,
                            Constants.EXTERRNALBACKUP_ERROR_MESSAGE.plus(it.exception_message),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    fun fullrecoverCustom(application: Application, data: Uri){

        viewModelScope.launch {
            recoverDatabaseCustomOption(application,data)
            delay(1500L)
            restoreDataFromBackUp(application)
        }

    }
    private suspend fun recoverDatabaseCustomOption(application: Application, data: Uri){
        viewModelScope.launch {
            recoverDatabaseCustomUseCase(data).collect{
                when(it)
                {
                    is BackUpState.Success ->{
                        Toast.makeText(application.applicationContext,
                            Constants.RECOVERY_SUCCESS_MESSAGE,Toast.LENGTH_SHORT).show()

                    }
                    is BackUpState.Loading -> {
                        Toast.makeText(application.applicationContext,
                            Constants.RECOVERY_LOADING_MESSAGE,Toast.LENGTH_SHORT).show()
                    }
                    is BackUpState.Error -> {
                        Toast.makeText(application.applicationContext,
                            Constants.EXTERRNALBACKUP_ERROR_MESSAGE.plus(it.exception_message),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



   private fun restoreDataFromBackUp(application: Application)
    {

        viewModelScope.launch {
            unpackRecoveryUseCase().collect{
                when(it)
                {
                    is BackUpState.Success ->{
                        Toast.makeText(application.applicationContext,
                            "restoration complete",Toast.LENGTH_SHORT).show()


                    }
                    is BackUpState.Loading -> {
                        Toast.makeText(application.applicationContext,
                            "loading restore",Toast.LENGTH_SHORT).show()
                    }
                    is BackUpState.Error -> {
                        Toast.makeText(application.applicationContext,
                            "restoration failure".plus(it.exception_message),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


   private  fun restart(activity: Activity) {
        val intent = Intent(activity.baseContext, MainActivity::class.java)
        activity.startActivity(intent)
        activity.finishAffinity()
    }

}