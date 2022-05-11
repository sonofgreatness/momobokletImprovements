package com.example.momobooklet_by_sm.database.UserRepository

import androidx.lifecycle.LiveData

import com.example.momobooklet_by_sm.database.UserDao
import com.example.momobooklet_by_sm.database.model.TransactionModel


class TransactionRepository(private val userDao: UserDao){


    val readAllTransactiondata:LiveData<List<TransactionModel>> =userDao.readAllTransactiondata()

    suspend fun addTransaction (transaction:TransactionModel){

        userDao.addTransaction(transaction)


    }

    suspend fun deleteAll(){
        userDao.deleteAll()



    }



    suspend fun removeTransaction(transaction:TransactionModel){

        userDao.removeTransaction(transaction)

    }


}