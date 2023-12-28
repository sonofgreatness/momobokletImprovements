package com.free.momobooklet_by_sm.domain.workers.remote.transactions

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class TransactionBackupManager(private val context: Context) {




    fun startBackup() {
        val exportWorkRequest = OneTimeWorkRequestBuilder<ExportTransactiondToFileWorker>()
                                .addTag(WORK_TAG)
                                .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadTransactionsWorker>()
                              .addTag(WORK_TAG)
                              .build()

        val workRequest = WorkManager.getInstance(context)
            .beginWith(exportWorkRequest)
            .then(uploadWorkRequest)
            .enqueue()

        // Optionally, observe the work request for progress
        observeProgress(WORK_TAG)
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
                }
            }
    }

    companion object {
        private const val WORK_TAG = "transactionBackupWork"
        private const val PROGRESS_KEY = "progress"
    }
}