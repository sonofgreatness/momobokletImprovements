package com.example.momobooklet_by_sm.database.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.momobooklet_by_sm.database.local.Database
import com.example.momobooklet_by_sm.database.local.models.BACKUP_METADATA
import com.example.momobooklet_by_sm.database.local.repositories.TransactionRepository


/************************************
 * Writes to GoogleSheet
 **********************************/
class ExportRoomDBWorker(val appContext: Context, params: WorkerParameters)
    :CoroutineWorker(appContext, params){
    /*----------------------------------------------------------------
    *READS ROOM DB TABLE  BACKUP_METADATA
    *Gets list of all transactions => main_list
    * if main_list.size > BACKUP_METADATA.previous_size
    *       create new list => upload_list
    *       upload_list = main_list[i] for i in range(BACKUP_METADATA.previous_size, main_list.size)
    *       upload elements of upload_list
    *else wait until condition is met (check again in 16 hours)
    *-------------------------------------------------------------*/
    override suspend fun doWork(): Result
    {
        val transactionDao = Database.getDatabase(appContext).getDao()
        val repository =  transactionDao.let { TransactionRepository(it) }

        val BACKUP_METADATA = repository.getTransactionMetaData()
        val main_list = repository.getAllTransactionsRegularData()


        return Result.success()
    }
}