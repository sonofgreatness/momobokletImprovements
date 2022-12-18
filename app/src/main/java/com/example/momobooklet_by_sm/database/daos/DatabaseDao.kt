package com.example.momobooklet_by_sm.database.daos

import androidx.room.*
import com.example.momobooklet_by_sm.database.models.TransactionModel


@Dao
interface DatabaseDao {

    @Insert
        ( onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTransaction(transaction:TransactionModel)
    @Query("SELECT * FROM RECORDS_SHEET ORDER BY Transaction_ID ASC")
    suspend   fun readAllTransactiondata () : List<TransactionModel>
    //@Query("SELECT*FROM RECORDS_SHEET ORDER BY Time ASC")
    @Delete
    fun removeTransaction(transaction:TransactionModel)
    // Remove all Transactions
    @Query("DELETE FROM RECORDS_SHEET")
    fun deleteAll()


}