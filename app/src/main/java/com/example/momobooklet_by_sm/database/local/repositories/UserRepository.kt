package com.example.momobooklet_by_sm.database.local.repositories

import com.example.momobooklet_by_sm.database.local.daos.UserAccountsDao
import com.example.momobooklet_by_sm.database.local.models.UserModel
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserAccountsDao) {

    fun readAllData(): Flow<List<UserModel>>
    {
        return userDao.readAllUserAccountsData()
    }


     fun readActiveuser() :Flow<List<UserModel>>
    {
        return userDao.readActiveUser()
    }

    suspend fun getActiveUser() = userDao.getActiveUser()
    suspend fun getAllUserAccounts() = userDao.getAllUserAccounts()

    suspend fun addUser(user: UserModel) {
        userDao.addUser(user)
    }

    suspend fun updateUser(user: UserModel) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: UserModel) {

        userDao.deleteUser(user)
    }

    fun deleteAllUsers() {
        userDao.deleteAllUsers()

    }
}
