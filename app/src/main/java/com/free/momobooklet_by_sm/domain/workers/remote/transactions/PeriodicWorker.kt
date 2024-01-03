package com.free.momobooklet_by_sm.domain.workers.remote.transactions

import android.content.Context
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import timber.log.Timber
import javax.inject.Inject

class PeriodicWorker @Inject constructor(
    val appContext: Context,
        params: WorkerParameters
) : CoroutineWorker(appContext,params) {
    override suspend fun doWork(): Result {


        return try {

           //Toast.makeText(appContext, "startbackupCalled", Toast.LENGTH_SHORT).show()//

            TransactionBackupManager(appContext).startBackup()
            Timber.d("main worker     -> success")
            Result.success()
        }catch (e:Exception) {
            Timber.d("main worker     -> ${e.message}")
            Result.failure()
        }


    }

}