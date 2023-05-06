package com.example.momobooklet_by_sm.data.local.daos

import androidx.room.*
import com.example.momobooklet_by_sm.data.local.models.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountsDao {

    @Update
    suspend fun updateUser(user: UserModel)
    @Insert
        ( onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser (user: UserModel)
    // Remove all  Accounts
    @Query("DELETE  FROM User_Accounts")
    fun deleteAllUsers()
    //Get user in control
    @Query("SELECT*FROM user_accounts WHERE IsIncontrol LIKE 1" )
    fun  readActiveUser():Flow<List<UserModel>>
    //fun readAll Transaction data_by date() :LiveData<List<TransactionModel>>

    @Delete
    suspend fun deleteUser(user:UserModel)
    /*@Query("DELETE FROM User_Accounts WHERE MoMoNumber = user.MoMoNumber")
    suspend fun deleteUser(user:UserModel)*/

    @Query("SELECT * FROM User_Accounts ORDER BY MoMoName  ASC")
    fun readAllUserAccountsData() : Flow<List<UserModel>>



    @Query("SELECT * FROM User_Accounts ORDER BY MoMoName  ASC")
    suspend fun getAllUserAccounts():List<UserModel>

    @Query("SELECT*FROM user_accounts WHERE IsIncontrol LIKE 1")
    suspend fun getActiveUser():List<UserModel>




}