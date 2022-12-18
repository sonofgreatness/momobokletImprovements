package com.example.momobooklet_by_sm.database.daos

import androidx.room.*
import com.example.momobooklet_by_sm.database.models.UserModel

@Dao
interface UserAccountsDao {




    @Update
    suspend fun updateUser(user: UserModel)
    @Insert
        ( onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser (user: UserModel)
    // Remove all  Accounts
    @Query("DELETE FROM User_Accounts")
    fun deleteAllUsers()
    //Get user in control
    @Query("SELECT*FROM user_accounts WHERE IsIncontrol LIKE 1" )
    suspend fun  getActiveUser():List<UserModel>
    //fun readAllTransactiondata_bydate() :LiveData<List<TransactionModel>>
    @Delete
    fun deleteUser(user:UserModel)
    @Query("SELECT * FROM User_Accounts ORDER BY MoMoName  ASC")
    suspend fun readAllData() : List<UserModel>

}