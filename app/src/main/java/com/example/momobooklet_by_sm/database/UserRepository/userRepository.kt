package com.example.momobooklet_by_sm.database.UserRepository

import androidx.lifecycle.LiveData
import com.example.momobooklet_by_sm.database.UserDao
import com.example.momobooklet_by_sm.database.model.TransactionModel
import com.example.momobooklet_by_sm.database.model.UserModel



class userRepository(private val userDao: UserDao){

    val readAllData: LiveData<List<UserModel>> = userDao.readAllData()



    suspend fun addUser(user: UserModel){
        userDao.addUser(user)
    }




    suspend fun updateUser(user:UserModel){
        userDao.updateUser(user)

    }
    suspend fun  deleteUser(user: UserModel){

        userDao.deleteUser(user)


    }








    suspend fun deleteAllUsers (){
        userDao.deleteAllUsers()




    }

}