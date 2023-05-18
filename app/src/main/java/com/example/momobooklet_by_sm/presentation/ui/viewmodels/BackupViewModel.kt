package com.example.momobooklet_by_sm.presentation.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.momobooklet_by_sm.domain.workers.remote.CleanupWorker
import com.example.momobooklet_by_sm.domain.workers.remote.DownloadWorker
import com.example.momobooklet_by_sm.domain.workers.remote.ImportToRoomWorker
import com.example.momobooklet_by_sm.common.util.Constants.Companion.AGENT_PHONENUMBER_KEY
import com.example.momobooklet_by_sm.common.util.Constants.Companion.TAG_OUTPUT
import com.example.momobooklet_by_sm.common.util.Constants.Companion.TRANSACTIONDATA_IMPORT_WORK_NAME
import com.example.momobooklet_by_sm.domain.repositories.RemoteTransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class BackupViewModel @Inject constructor(val application: Application,
                                          val remoteTransactionsRepository: RemoteTransactionsRepository
                                          ):ViewModel()  {

    /********************************************************************************************
     * Handles Import/ Export  data  from and to Remote DB
     *    GoogleSpreadSheet -> To be replaced by JDBC(postgres - managed)
     *                       ->  but first we do Mongo DB
     ***********************************************************************************************/
    private var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    private val workManager = WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    init{
      print("okBackUpViewModel")
    }

    /*******************************************************************
     *===========================================================
     * IMPORT SECTION ->
     * The work can be expressed as the sum of 3 functions
     *   One to download JSON Data from GoogleSheet
     *   One to Save  downloaded Data to a file in Memory,
     *      and
     *   One to Read Data saved in a file, parse read data
     *        and   store in RoomDB, then delete Read file
     *   *******************************************************
     *   function downloadJSONData()
     *   function saveJSONDataToFile()
     *   function writeToRoomDFromFile()
     *
     *==============================================================
     ****************************************************************/

    internal fun importDataSet() {

        // Add WorkRequest to Cleanup temporary images
        var continuation = workManager
            .beginUniqueWork(
                TRANSACTIONDATA_IMPORT_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            )
        val downloadBuilder = OneTimeWorkRequestBuilder<DownloadWorker>()
        downloadBuilder.setInputData(workDataOf(AGENT_PHONENUMBER_KEY to "76911464"))
        val importToRoomBuilder = OneTimeWorkRequestBuilder<ImportToRoomWorker>()
        continuation = continuation.then(downloadBuilder.build())
        continuation = continuation.then(importToRoomBuilder.build())
        continuation.enqueue()



       /*val myWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<CleanupWorker>()
                .setInputData(workDataOf(AGENT_PHONENUMBER_KEY to "76911464"))
                .build()
            workManager.enqueue(myWorkRequest)*/
       }
}