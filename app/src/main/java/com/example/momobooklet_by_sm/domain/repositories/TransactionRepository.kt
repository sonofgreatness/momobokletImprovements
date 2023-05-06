package com.example.momobooklet_by_sm.domain.repositories

import com.example.momobooklet_by_sm.data.local.models.BACKUP_METADATA
import com.example.momobooklet_by_sm.data.local.models.TransactionModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {


    fun readAllTransactiondata(): Flow<List<TransactionModel>>

    fun searchTransactions(query: String) : Flow<List<TransactionModel>>

    fun  getSellTransactions(): Flow<List<TransactionModel>>

    fun getBuyTransactions(): Flow<List<TransactionModel>>

    fun getTodaysTransactions(date:String): Flow<List<TransactionModel>>

    suspend fun getDailyTransactions(date:String): List<TransactionModel>

    suspend fun addTransaction (transaction: TransactionModel)

    fun deleteAll()

    fun removeTransaction(transaction: TransactionModel)



    /*----------------------------------------------------------------------
                            BACKUP_META DATA
 -----------------------------------------------------------------------*/
    fun  clearTransactionMetaData()

    suspend fun  getTransactionMetaData():List<BACKUP_METADATA>

    suspend fun addTransactionMetaData(data: BACKUP_METADATA)

    suspend fun getAllTransactionsRegularData(): List<TransactionModel>




}