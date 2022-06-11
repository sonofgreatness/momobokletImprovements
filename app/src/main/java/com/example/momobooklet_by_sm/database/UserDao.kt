package com.example.momobooklet_by_sm.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.momobooklet_by_sm.database.model.TransactionModel
import com.example.momobooklet_by_sm.database.model.UserModel


@Dao
interface UserDao {

    @Update
    suspend fun updateUser(user:UserModel)


    @Insert
        ( onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser (user: UserModel)


    @Insert
        ( onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTransaction(transaction:TransactionModel)

    @Query("SELECT*FROM User_Accounts ORDER BY MoMoName  ASC")
    fun readAllData() : LiveData<List<UserModel>>

    @Query("SELECT*FROM RECORDS_SHEET ORDER BY Transaction_ID ASC")
    fun readAllTransactiondata () : LiveData<List<TransactionModel>>
    @Query("SELECT*FROM RECORDS_SHEET ORDER BY Time ASC")
    fun readAllTransactiondata_bydate() :LiveData<List<TransactionModel>>
    @Delete
    fun deleteUser(user:UserModel)

   @Delete
     fun removeTransaction(transaction:TransactionModel)

    // Remove all Transactions
    @Query("DELETE FROM RECORDS_SHEET")
     fun deleteAll()
    // Remove all  Accounts
    @Query("DELETE FROM User_Accounts")
    fun deleteAllUsers()
// SPECIFY QUERY  TO PERFORMS  SEARCHES HERE :




}