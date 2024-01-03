package com.free.momobooklet_by_sm.domain.workers.remote.transactions

import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.domain.workers.remote.ImportToRoomWorker
import com.free.momobooklet_by_sm.domain.workers.remote.transactions.download.DownloadTransactionsWorker
import com.free.momobooklet_by_sm.domain.workers.remote.transactions.download.ImportTransactionsWorker
import com.free.momobooklet_by_sm.domain.workers.remote.transactions.upload.ExportTransactiondToFileWorker
import com.free.momobooklet_by_sm.domain.workers.remote.transactions.upload.UploadTransactionsWorker
import timber.log.Timber
import kotlin.random.Random

class TransactionBackupManager(private val context: Context) {




    fun startBackup() {

        val exportWorkRequest = OneTimeWorkRequestBuilder<ExportTransactiondToFileWorker>()
                                .addTag(WORK_TAG)
                                .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadTransactionsWorker>()
                              .addTag(WORK_TAG_2)
                              .build()

        val workRequest = WorkManager.getInstance(context)
            .beginWith(exportWorkRequest)
            .then(uploadWorkRequest)
            .enqueue()

        // Optionally, observe the work request for progress
        observeProgress(WORK_TAG_2)
    }


    fun downloadTransactionData(){

        Timber.d ("downloadTransactionDataFromTransactionBackupManager")
        // Add WorkRequest to Cleanup temporary images
        var continuation = WorkManager.getInstance(context)
            .beginUniqueWork(
                Constants.TRANSACTIONDATA_IMPORT_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(DownloadTransactionsWorker::class.java)
            )

        val importTransactionsBuilder =
            OneTimeWorkRequestBuilder<ImportTransactionsWorker>().build()

        continuation
            .then(importTransactionsBuilder)
            .enqueue()

    }


    fun stopBackup() {
        // Cancel all work with the given tag
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
    }

    private fun observeProgress(workTag:String) {
        WorkManager.getInstance(context).getWorkInfosByTagLiveData(workTag)
            .observeForever { workInfo ->
                if (workInfo != null) {
                    val progress = workInfo.last().progress.getInt(PROGRESS_KEY, 0)
                    // Update UI with progress information
                    // ...
                    Toast.makeText(context,"workStarted $progress", Toast.LENGTH_SHORT).show()
                }
            }
    }




    companion object {
        private const val WORK_TAG = "transactionBackupWork"
        private const val WORK_TAG_2 = "transactionBackupWorkToo"
        private const val PROGRESS_KEY = "progress"
    }
}