package com.example.momobooklet_by_sm.database.ViewModel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.momobooklet_by_sm.database.UserDao
import com.example.momobooklet_by_sm.database.UserDatabase
import com.example.momobooklet_by_sm.database.UserRepository.userRepository
import com.example.momobooklet_by_sm.database.model.TransactionModel
import com.example.momobooklet_by_sm.database.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

   abstract  class UserViewModel(
    application: Application):AndroidViewModel(application) {



    val readAllData: LiveData<List<UserModel>>

    private val repository: userRepository



    init {
        val userDao = UserDatabase.getDatabase(
            application
        ).userDao()
        repository = userRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addUser(user: UserModel) {


        viewModelScope.launch(Dispatchers.IO) {


            repository.addUser(user)
        }

    }





    fun deleteUser(user:UserModel){
        viewModelScope.launch(Dispatchers.IO){

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

}
