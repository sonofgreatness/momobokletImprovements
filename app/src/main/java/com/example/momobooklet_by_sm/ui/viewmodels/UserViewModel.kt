package com.example.momobooklet_by_sm.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.momobooklet_by_sm.database.models.Database
import com.example.momobooklet_by_sm.database.models.TransactionModel
import com.example.momobooklet_by_sm.database.models.UserModel
import com.example.momobooklet_by_sm.database.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel(
    application: Application
) : AndroidViewModel(application) {


    val _readAllData = MutableLiveData<List<UserModel>>()
    val readAllData: LiveData<List<UserModel>>
        get() = _readAllData
    private val repository: UserRepository

    init {
        val userDao = Database.getDatabase(
            application
        )?.getUserAccountsDao()
        repository = userDao?.let { UserRepository(it) }!!
        viewModelScope.launch {

                _readAllData.postValue(repository.readAllData())
                Timber.e("viewM->${repository.readAllData()}")

        }
    }

    fun addUser(user: UserModel) {


        viewModelScope.launch {
            repository.addUser(user)
        }

    }

    fun deleteUser(user: UserModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(user)
        }
    }

    fun updateUser(user: UserModel) {


        viewModelScope.launch(Dispatchers.IO) {

            repository.updateUser(user)
        }
    }


    fun deleteAllUsers() {

        viewModelScope.launch(Dispatchers.IO) {


            repository.deleteAllUsers()


        }
    }

    fun getActiveUser() {

        viewModelScope.launch {
            repository.getActiveuser().let {
                _readAllData.postValue(it)
                Timber.e("viewMgetAll->$it")

            }
        }
    }
}