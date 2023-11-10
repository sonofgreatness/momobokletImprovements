package com.free.momobooklet_by_sm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.free.momobooklet_by_sm.domain.repositories.RemoteTransactionsRepository
import com.free.momobooklet_by_sm.domain.workers.factories.MyWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BookletApplication :Application() , Configuration.Provider{
    @Inject
    lateinit var remoteTransactionsRepository: RemoteTransactionsRepository

    init {

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

    }
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                "File download",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {

        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(MyWorkerFactory(remoteTransactionsRepository))


     return    Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(MyWorkerFactory(remoteTransactionsRepository))
            .build()
    }
}
