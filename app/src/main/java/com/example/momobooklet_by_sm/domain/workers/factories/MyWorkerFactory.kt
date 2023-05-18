package com.example.momobooklet_by_sm.domain.workers.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.momobooklet_by_sm.domain.repositories.RemoteTransactionsRepository
import com.example.momobooklet_by_sm.domain.workers.remote.DownloadWorker
import javax.inject.Inject

/*****************************
 * For DownloadWorker
 *******************************/
/* Copyright 2020 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */
class MyWorkerFactory constructor( private val remoteTransactionsRepository: RemoteTransactionsRepository) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
     //   return DownloadWorker( remoteTransactionsRepository, appContext, workerParameters)


        return when(workerClassName) {
            DownloadWorker::class.java.name ->
                DownloadWorker(remoteTransactionsRepository,appContext, workerParameters)
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }


    }
}