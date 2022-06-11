package com.example.momobooklet_by_sm.database.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.momobooklet_by_sm.database.UserDatabase
import com.example.momobooklet_by_sm.database.UserRepository.TransactionRepository
import com.example.momobooklet_by_sm.database.UserRepository.userRepository

import com.example.momobooklet_by_sm.database.model.TransactionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel (   application: Application,

                                        ):AndroidViewModel(application) {


    val readAllTransactiondata_bydate: LiveData<List<TransactionModel>>
    val readAllTransactiondata: LiveData<List<TransactionModel>>
    private val repository: TransactionRepository

    init {
        val userDao = UserDatabase.getDatabase(application)?.userDao()
        repository = userDao?.let { TransactionRepository(it) }!!
        readAllTransactiondata = repository.readAllTransactiondata
        readAllTransactiondata_bydate=repository.readAllTransactiondata_bydate
    }


    fun addTransaction(transaction: TransactionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTransaction(transaction)
        }

    }


    fun removeTransaction(transaction: TransactionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeTransaction(transaction)
        }

    }


    fun deleteAll() {

        viewModelScope.launch(Dispatchers.IO) {


            repository.deleteAll()


        }
    }

}