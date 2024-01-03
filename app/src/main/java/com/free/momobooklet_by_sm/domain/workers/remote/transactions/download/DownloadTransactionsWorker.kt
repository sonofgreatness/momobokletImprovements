package com.free.momobooklet_by_sm.domain.workers.remote.transactions.download

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.free.momobooklet_by_sm.domain.use_cases.manage_transactions.DownloadTransactionsUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.AuthenticateUserInBackEndUseCase
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

/********************************************************
 * Downloads Transaction data and stores
 *     it  cache folder.
 *     filename == DOWNLOADED_TRANSACTIONS_FILE
 ********************************************************/
class DownloadTransactionsWorker

@Inject constructor(
    private val downloadTransactionsUseCase: DownloadTransactionsUseCase,
    private val userRepository: UserRepository,
    private  val authenticateUserInBackEndUseCase: AuthenticateUserInBackEndUseCase,
    val appContext: Context,
    params: WorkerParameters
) :
    CoroutineWorker(appContext,params)
{
    override suspend fun doWork(): Result {
        Timber.d("downlOad transactionWorker Called")
        startForegroundService()
        val listOfUsers = userRepository.getAllUserAccounts()
        try {
            Timber.d("downlOad transactionWorker Called")



            //authenticates all users  in server

            listOfUsers.forEach {
                val request = AuthenticationRequest(
                    username = it.MoMoNumber,
                    password = it.AgentPassword)

                authenticateUserInBackEndUseCase(request).collect{ resource ->
                    when(resource){
                        is Resource.Error -> {
                            Result.retry()
                        }
                        else -> {

                        }
                    }
                }
            }


            listOfUsers.forEach {

                downloadTransactionsUseCase(it.MoMoNumber).collect{ resource ->
                    when(resource){
                        is Resource.Error -> {
                            Result.failure()
                        }
                        else -> {

                        }
                    }
                }
            }




        }catch (e:Exception)
        {
            Timber.d("downLoad worker failed =>  $e")
                 return Result.failure()
        }

            return  Result.success()
    }

    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(appContext, "download_channel")
                    .setSmallIcon(R.drawable.download_icon)
                    .setContentText("Downloading Transactions")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }

}