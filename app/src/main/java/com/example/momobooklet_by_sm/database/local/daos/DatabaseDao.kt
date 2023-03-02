package com.example.momobooklet_by_sm.database.local.daos

import androidx.room.*
import com.example.momobooklet_by_sm.database.local.models.TransactionModel
import kotlinx.coroutines.flow.Flow


@Dao
interface DatabaseDao {

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


    @Query("SELECT * FROM RECORDS_SHEET WHERE Date = :date ")
    fun getTodaysTransactions(date:String): Flow<List<TransactionModel>>


    @Query("SELECT * FROM RECORDS_SHEET WHERE Date = :date ")
     suspend fun getDailyTransactions(date:String): List<TransactionModel>


    @Query("SELECT * FROM RECORDS_SHEET")
    suspend fun getAllTransactionsRegularData(): List<TransactionModel>


}