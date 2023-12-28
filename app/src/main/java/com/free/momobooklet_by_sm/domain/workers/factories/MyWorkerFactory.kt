package com.free.momobooklet_by_sm.domain.workers.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.free.momobooklet_by_sm.domain.repositories.RemoteTransactionsRepository
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.free.momobooklet_by_sm.domain.use_cases.manage_transactions.UploadTransactionsUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.AuthenticateUserInBackEndUseCase
import com.free.momobooklet_by_sm.domain.workers.remote.DownloadWorker
import com.free.momobooklet_by_sm.domain.workers.remote.transactions.ExportTransactiondToFileWorker
import com.free.momobooklet_by_sm.domain.workers.remote.transactions.UploadTransactionsWorker
import java.security.PrivateKey

/*****************************
 * For DownloadWorker
 *******************************/
/* Copyright 2020 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */
class MyWorkerFactory constructor(
    private val remoteTransactionsRepository: RemoteTransactionsRepository,
    private val transactionRepository: TransactionRepository,
    private  val userRepository: UserRepository,
    private val uploadTransactionsUseCase: UploadTransactionsUseCase,
    private  val authenticateUserInBackEndUseCase: AuthenticateUserInBackEndUseCase
    ) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
     //   return DownloadWorker( remoteTransactionsRepository, appContext, workerParameters)


        return when(workerClassName) {
            DownloadWorker::class.java.name ->
                DownloadWorker(remoteTransactionsRepository,appContext, workerParameters)

            ExportTransactiondToFileWorker::class.java.name ->
                ExportTransactiondToFileWorker(transactionRepository,userRepository,appContext, workerParameters)

            UploadTransactionsWorker::class.java.name ->
                UploadTransactionsWorker(uploadTransactionsUseCase, authenticateUserInBackEndUseCase,userRepository,appContext, workerParameters)
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }
    }
}