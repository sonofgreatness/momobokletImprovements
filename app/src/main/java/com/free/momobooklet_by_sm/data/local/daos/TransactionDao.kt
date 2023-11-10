package com.free.momobooklet_by_sm.data.local.daos

import androidx.room.*
import com.free.momobooklet_by_sm.data.local.models.BACKUP_METADATA
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {

    @Insert
        ( onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTransaction(transaction:TransactionModel)

    @Query("""SELECT * FROM RECORDS_SHEET""")

    fun readAllTransactiondata () : Flow<List<TransactionModel>>



    @Query("""SELECT * FROM RECORDS_SHEET
            JOIN RECORD_SHEET_FTS  ON  RECORDS_SHEET.Transaction_ID = RECORD_SHEET_FTS.Transaction_ID
            WHERE RECORD_SHEET_FTS MATCH :query""")
    fun searchTransactions(query: String) : Flow<List<TransactionModel>>


    @Query("""
            SELECT * FROM RECORDS_SHEET WHERE Transaction_type = 1
            """)
    fun getBuyTransactions():Flow<List<TransactionModel>>

    @Query("""
            SELECT * FROM RECORDS_SHEET WHERE Transaction_type = 0
           """)
    fun getSellTransactions():Flow<List<TransactionModel>>

    @Delete
    fun removeTransaction(transaction:TransactionModel)

    // Remove all Transactions
    @Query("DELETE FROM RECORDS_SHEET")
    fun deleteAll()

    @Query("SELECT * FROM RECORDS_SHEET WHERE Date = :date AND AgentPhoneNumber =:momoNumber")
    fun getTodaysTransactions(date:String, momoNumber: String): Flow<List<TransactionModel>>

    @Query("SELECT * FROM RECORDS_SHEET WHERE Date = :date AND AgentPhoneNumber =:momoNumber")
     suspend fun getDailyTransactions(date:String, momoNumber: String): List<TransactionModel>

    @Query("SELECT * FROM RECORDS_SHEET WHERE AgentPhoneNumber =:momoNumber")
    suspend fun getAllTransactionsRegularData(momoNumber: String): List<TransactionModel>

    @Query("SELECT * FROM RECORDS_SHEET")
    suspend fun getAllTransactionsRegularData_All(): List<TransactionModel>



    /*----------------------------------------------------------------------
                                BACKUP_META DATA
     -----------------------------------------------------------------------*/
    @Query("DELETE FROM BACKUP_METADATA")
    fun  clearTransactionMetaData()

    @Query("SELECT *  FROM BACKUP_METADATA")
    suspend fun  getTransactionMetaData():List<BACKUP_METADATA>

    @Insert
        ( onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTransactionMetaData(data:BACKUP_METADATA)

}