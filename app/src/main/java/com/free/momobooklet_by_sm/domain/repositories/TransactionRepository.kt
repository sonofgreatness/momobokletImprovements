package com.free.momobooklet_by_sm.domain.repositories

import com.free.momobooklet_by_sm.data.local.models.BACKUP_METADATA
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {


    fun readAllTransactiondata(momoNumber: String): Flow<List<TransactionModel>>
    fun searchTransactions(query: String) : Flow<List<TransactionModel>>
    fun  getSellTransactions(): Flow<List<TransactionModel>>

    fun getBuyTransactions(): Flow<List<TransactionModel>>
    fun getTodaysTransactions(date:String, momoNumber: String): Flow<List<TransactionModel>>
    suspend fun getDailyTransactions(date:String, momoNumber: String): List<TransactionModel>

    suspend fun addTransaction (transaction: TransactionModel)
    fun deleteAll()
    fun removeTransaction(transaction: TransactionModel)

    suspend fun getNewestTransactions(startDate :Long , now: Long, momoNumber: String): List<TransactionModel>





    /*----------------------------------------------------------------------
                            BACKUP_META DATA
    -----------------------------------------------------------------------*/
    fun  clearTransactionMetaData()
    suspend fun  getTransactionMetaData():List<BACKUP_METADATA>
    suspend fun addTransactionMetaData(data: BACKUP_METADATA)
    suspend fun getAllTransactionsRegularData(momoNumber: String): List<TransactionModel>
    suspend fun getAllTransactionsRegularData_All(): List<TransactionModel>


}