package com.example.momobooklet_by_sm.data.local.repositories

import com.example.momobooklet_by_sm.data.local.daos.UserAccountsDao
import com.example.momobooklet_by_sm.data.local.models.UserModel
import com.example.momobooklet_by_sm.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userDao: UserAccountsDao): UserRepository{

    override fun readAllData(): Flow<List<UserModel>>
    {
        return userDao.readAllUserAccountsData()
    }


     override fun readActiveuser() :Flow<List<UserModel>>
    {
        return userDao.readActiveUser()
    }

    override suspend fun getActiveUser() = userDao.getActiveUser()
    override suspend fun getAllUserAccounts() = userDao.getAllUserAccounts()
    override suspend fun addUser(user: UserModel) {
        userDao.addUser(user)
    }
    override suspend fun updateUser(user: UserModel) {
        userDao.updateUser(user)
    }
    override suspend fun deleteUser(user: UserModel) {

        userDao.deleteUser(user)
    }
    override fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }
}
