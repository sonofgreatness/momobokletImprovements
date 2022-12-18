package com.example.momobooklet_by_sm.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.momobooklet_by_sm.database.models.Database
import com.example.momobooklet_by_sm.database.models.TransactionModel
import com.example.momobooklet_by_sm.database.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class TransactionViewModel(
    application: Application

) : AndroidViewModel(application) {

    val _searchResults = MutableLiveData<List<TransactionModel>>()
    val searchResults: LiveData<List<TransactionModel>>
        get() = _searchResults

    private val repository: TransactionRepository


    init {
        val userDao = Database.getDatabase(application)?.getDao()
        repository = userDao?.let { TransactionRepository(it) }!!
        viewModelScope.launch {
            _searchResults.postValue(repository.readAllTransactiondata())
            Timber.e("t_viewM->${repository.readAllTransactiondata()}")


        }
    }

    fun getAllTransaction() = viewModelScope.launch {
        repository.readAllTransactiondata().let {
            _searchResults.postValue(it)
        }
    }





    fun addTransaction(transaction: TransactionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTransaction(transaction)
            Timber.e("add_transaction called in viewmodel")
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