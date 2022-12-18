package com.example.momobooklet_by_sm.database.repositories

import com.example.momobooklet_by_sm.database.daos.DatabaseDao
import com.example.momobooklet_by_sm.database.models.TransactionModel



class TransactionRepository(private val userDao: DatabaseDao){

    suspend fun readAllTransactiondata() =userDao.readAllTransactiondata()

  //  val readAllTransactiondata_bydate: LiveData<List<TransactionModel>> =userDao.readAllTransactiondata_bydate()

    suspend fun addTransaction (transaction:TransactionModel){
        userDao.addTransaction(transaction)

    }

     fun deleteAll(){
        userDao.deleteAll()
    }

    fun removeTransaction(transaction:TransactionModel){
        userDao.removeTransaction(transaction)
    }



}