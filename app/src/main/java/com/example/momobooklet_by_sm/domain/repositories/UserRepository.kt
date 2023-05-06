package com.example.momobooklet_by_sm.domain.repositories

import com.example.momobooklet_by_sm.data.local.models.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun readAllData(): Flow<List<UserModel>>
    fun readActiveuser() : Flow<List<UserModel>>

    suspend fun getActiveUser():List<UserModel>
    suspend fun getAllUserAccounts():List<UserModel>

    suspend fun addUser(user: UserModel)
    suspend fun updateUser(user: UserModel)

    suspend fun deleteUser(user: UserModel)
    fun deleteAllUsers()
}