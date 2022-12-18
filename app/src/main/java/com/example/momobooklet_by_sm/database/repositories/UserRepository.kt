package com.example.momobooklet_by_sm.database.repositories

import com.example.momobooklet_by_sm.database.daos.UserAccountsDao
import com.example.momobooklet_by_sm.database.models.UserModel

class UserRepository(private val userDao: UserAccountsDao) {

    suspend fun readAllData() = userDao.readAllData()


    suspend fun getActiveuser() = userDao.getActiveUser()

    suspend fun addUser(user: UserModel) {
        userDao.addUser(user)
    }

    suspend fun updateUser(user: UserModel) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: UserModel) {

        userDao.deleteUser(user)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()

    }
}
