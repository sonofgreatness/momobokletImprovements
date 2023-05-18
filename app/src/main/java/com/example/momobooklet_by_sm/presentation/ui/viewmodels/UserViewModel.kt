package com.example.momobooklet_by_sm.presentation.ui.viewmodels

import androidx.lifecycle.*
import com.example.momobooklet_by_sm.data.local.models.UserModel
import com.example.momobooklet_by_sm.data.local.repositories.UserRepositoryImpl
import com.example.momobooklet_by_sm.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
     val userRepository: UserRepository
) : ViewModel() {

    val _userInControl =MutableLiveData<List<UserModel>>()
    val userInControl  : LiveData<List<UserModel>>
            get() = _userInControl

    val _readAllData = MutableLiveData<List<UserModel>>()
    val readAllData: LiveData<List<UserModel>>
        get() = _readAllData

    val _registrationSmSBody = MutableLiveData<String>()
    val registrationSmSBody :LiveData<String> get () = _registrationSmSBody


    init {
        getPsuedoActiveUser()
        viewModelScope.launch {
              userRepository.readAllData().collect {
                  _readAllData.postValue(it)
              }
        }
    }

    fun addUser(user: UserModel) {
        viewModelScope.launch {
            userRepository.addUser(user)
        }
    }
    fun deleteUser(user: UserModel) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteUser(user)
        }
    }
    fun updateUser(user: UserModel) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUser(user)
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteAllUsers()
        }
    }

    fun getActiveUsers() {
        viewModelScope.launch {
            userRepository.readActiveuser().collect {
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
            var activeUsers  = userRepository.getActiveUser()
            if (activeUsers == null)
                _userInControl.postValue(activeUsers)
                else {
                activeUsers = userRepository.getAllUserAccounts()
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