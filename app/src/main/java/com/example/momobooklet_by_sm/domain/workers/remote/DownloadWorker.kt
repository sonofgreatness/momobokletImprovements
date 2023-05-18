package com.example.momobooklet_by_sm.domain.workers.remote

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.Constants.Companion.AGENT_PHONENUMBER_KEY
import com.example.momobooklet_by_sm.domain.repositories.RemoteTransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.random.Random

class DownloadWorker @Inject constructor(
     private val remoteTransactionsRepository: RemoteTransactionsRepository,
      val appContext: Context,
     params: WorkerParameters) :
    CoroutineWorker(appContext,params) {

    override suspend fun doWork(): Result {
        Log.d("downloadWorker", "begin")
        val agentPhoneNumber =   inputData.getString(AGENT_PHONENUMBER_KEY)

        startForegroundService()
        delay(5000L)
        Log.d("downloadWorker1.5", "begin+++")

        var response :Response<ResponseBody>?

        try {
            Log.d("downloadWorker 1.7","before requestMade")
            response= remoteTransactionsRepository.getTransactions(mapOf("AgentPhoneNumber" to agentPhoneNumber!!))
        }catch (ex:HttpException)
        {

            response = null
            Log.d("downloadWorker 1.8","message :: -> ${ex.localizedMessage}")
        //remoteTransactionsRepository.getTransactions(mapOf("AgentPhoneNumber" to agentPhoneNumber!!))
        }
        Log.d("downloadWorker1.9", "begin++plus")
        response?.body()?.let { body ->
            Log.d("downloadWorker2", "mid ")
            return withContext(Dispatchers.IO) {

                val file = File(appContext.cacheDir, "transactions.json")
                val outputStream = FileOutputStream(file)
                outputStream.use { stream ->
                    try {
                        stream.write(body.bytes())
                    } catch(e: IOException) {
                        return@withContext Result.failure(
                            workDataOf(
                                WorkerKeys.ERROR_MSG to e.localizedMessage
                            )
                        )
                    }
                }

                //java.util.concurrent.ExecutionException
                Result.success(
                    workDataOf(
                        WorkerKeys.JSON_URI to file.toUri().toString()
                    )
                )
            }

        }
        if (!response?.isSuccessful!!) {
            if (response?.code().toString().startsWith("5")) {
                return Result.retry()
            }
            return Result.failure(
                workDataOf(
                    WorkerKeys.ERROR_MSG to "Network error"
                )
            )
        }
        return Result.failure(
            workDataOf(WorkerKeys.ERROR_MSG to "Unknown error")
        )
    }

    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(appContext, "download_channel")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("Downloading...")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }

}