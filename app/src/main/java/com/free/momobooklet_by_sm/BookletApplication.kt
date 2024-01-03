package com.free.momobooklet_by_sm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.free.momobooklet_by_sm.domain.repositories.RemoteTransactionsRepository
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.free.momobooklet_by_sm.domain.use_cases.manage_transactions.DownloadTransactionsUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_transactions.UploadTransactionsUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.AuthenticateUserInBackEndUseCase
import com.free.momobooklet_by_sm.domain.workers.factories.MyWorkerFactory
import com.free.momobooklet_by_sm.domain.workers.remote.transactions.PeriodicWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class BookletApplication :Application() , Configuration.Provider{
    @Inject
    lateinit var remoteTransactionsRepository: RemoteTransactionsRepository

    @Inject
    lateinit var transactionRepository: TransactionRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var uploadTransactionsUseCase: UploadTransactionsUseCase

    @Inject
    lateinit var authenticateUserInBackEndUseCase: AuthenticateUserInBackEndUseCase

   @Inject
   lateinit var downloadTransactionsUseCase:DownloadTransactionsUseCase


    init {

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

    }
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "download_channel",
            "File download",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)


        //initiate daily transaction backup
        val periodicStartingPointWorkRequest = PeriodicWorkRequestBuilder<PeriodicWorker>(
            repeatInterval = 23, // Repeat every 23 hours
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(
                5, TimeUnit.MINUTES) // Start after 1 hour
            .build()
        WorkManager.getInstance(this).enqueue(periodicStartingPointWorkRequest)



    }

    override fun getWorkManagerConfiguration(): Configuration {

        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(MyWorkerFactory(
            remoteTransactionsRepository,
            transactionRepository,
            userRepository,
            uploadTransactionsUseCase,
            downloadTransactionsUseCase,
            authenticateUserInBackEndUseCase))



     return    Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(
                MyWorkerFactory(
                    remoteTransactionsRepository,
                    transactionRepository,
                    userRepository,
                    uploadTransactionsUseCase,
                    downloadTransactionsUseCase,
                    authenticateUserInBackEndUseCase
            ))
            .build()
    }
}
