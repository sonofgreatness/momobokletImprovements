package com.example.momobooklet_by_sm.presentation.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.momobooklet_by_sm.data.local.Database
import com.example.momobooklet_by_sm.data.local.models.UserModel
import com.example.momobooklet_by_sm.data.local.repositories.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class UserViewModel(
    application: Application
) : AndroidViewModel(application) {

    val _userInControl =MutableLiveData<List<UserModel>>()
    val userInControl  : LiveData<List<UserModel>>
            get() = _userInControl

    val _readAllData = MutableLiveData<List<UserModel>>()
    val readAllData: LiveData<List<UserModel>>
        get() = _readAllData

    val _registrationSmSBody = MutableLiveData<String>()
    val registrationSmSBody :LiveData<String> get () = _registrationSmSBody

    private val repository: UserRepositoryImpl

    init {
        val userDao = Database.getDatabase(
            application
        ).getUserAccountsDao()
        repository = userDao.let { UserRepositoryImpl(it) }!!
        getPsuedoActiveUser()
        viewModelScope.launch {
              repository.readAllData().collect {
                  _readAllData.postValue(it)
              }
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

    fun getActiveUsers() {
        viewModelScope.launch {
            repository.readActiveuser().collect {
                _userInControl.postValue(it)
                Timber.e("viewMgetAll->$it")
            }
        }
    }
    /*******************************************
     * Sets _useIncontrol list with list
     *                  of active users
     *     or list of users if none are Active
     *     NB: ideally only one user should be in control (Active)
     ***********************************************/
    private fun getPsuedoActiveUser()
    {
        viewModelScope.launch {
            var activeUsers  = repository.getActiveUser()
            if (activeUsers == null)
                _userInControl.postValue(activeUsers)
                else {
                activeUsers = repository.getAllUserAccounts()
                  _userInControl.postValue(activeUsers)
            }
        }
    }


    fun setregistrastionSMSBody(text : String)
    {
        viewModelScope.launch {
            _registrationSmSBody.postValue(text)
        }
    }
}