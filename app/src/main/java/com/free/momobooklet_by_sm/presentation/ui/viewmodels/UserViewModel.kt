package com.free.momobooklet_by_sm.presentation.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.FireBaseRegistrationState
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
import com.free.momobooklet_by_sm.data.local.models.UserModel
import com.free.momobooklet_by_sm.domain.repositories.ConnectivityObserver
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.free.momobooklet_by_sm.domain.repositories.user.BackEndUserRepository
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.RegisterUserInFirebaseUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.SignInUserInFirebaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val userRepository: UserRepository,
    val connectivityObserver: ConnectivityObserver,
    private val registerUserInFirebaseUseCase: RegisterUserInFirebaseUseCase,
    private val signInUserInFirebaseUseCase: SignInUserInFirebaseUseCase,
    private val backEndUserRepositoryImpl: BackEndUserRepository
) : ViewModel() {


    sealed class MyState {
        object Fetched : MyState()
        object Error : MyState()
    }






    private val _usableState = MutableLiveData<MyState>()
    val usableState: LiveData<MyState>
        get() = _usableState

    /**
     * Useful Too , just not now
    * */
    val state =
        connectivityObserver.observe()
            .map(
                onUnavailable = { MyState.Error },
                onAvailable = { MyState.Fetched },
            ).asLiveData(Dispatchers.IO)

    private val _userInControl = MutableLiveData<List<UserModel>>()
    val userInControl: LiveData<List<UserModel>>
        get() = _userInControl

    private val _readAllData = MutableLiveData<List<UserModel>>()
    val readAllData: LiveData<List<UserModel>>
        get() = _readAllData


    private   val  _registrationStateTobeUsedOnMainThread = MutableLiveData<FireBaseRegistrationState>()
            val registrationStateTobeUsedOnMainThread : LiveData<FireBaseRegistrationState>
            get() = _registrationStateTobeUsedOnMainThread


    private  val _signInStateTobeUsedOnMainThread = MutableLiveData<FireBaseRegistrationState>()
      val signInStateTobeUsedOnMainThread : LiveData<FireBaseRegistrationState>
      get () = _signInStateTobeUsedOnMainThread



    init {

        getPsuedoActiveUser()
        viewModelScope.launch {
            userRepository.readAllData().collect {
                _readAllData.postValue(it)
            }
        }

        setActiveUsers()


      viewModelScope.launch {
              connectivityObserver.observe()
                  .map(
                      onUnavailable = { MyState.Error },
                      onAvailable = { MyState.Fetched },
                  ).collect{
                      _usableState.postValue(it) }

      }
        viewModelScope.launch {
            _registrationStateTobeUsedOnMainThread.postValue(FireBaseRegistrationState.Loading)
        _signInStateTobeUsedOnMainThread.postValue(FireBaseRegistrationState.Loading)
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

    /***********************************************************************
     * updateUser -> updates  a  UserModel in local database
     *      if  the UserModel has  IsIncontrol value = true
     *        set all other UserModel.IsInControll = false
     *      if  the UserModel has  IsIncontrol value = false
     *        set any other  UserModel, IsInControl value = true
     *@param user : UserModel  to be updated
     ************************************************************************/
    @SuppressLint("LogNotTimber")
    fun updateUser(user: UserModel) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                if (user.IsIncontrol)
                    setAllOtherUsersToInactive(user.MoMoNumber.trim())
                else
                    setAnyOtherUserToActive(user.MoMoNumber.trim())
                userRepository.updateUser(user)

            }
            catch (ex:Exception){
                Log.d("updated user callback failed", "${ex.message}  ")
            }

          try {
              val listOfAllUsers = userRepository.getAllUserAccounts()
              if(listOfAllUsers.size == 1)
                  setUserIncontrol(user)
          } catch (ex2: Exception) {

              Log.d("updated user callback2 failed", "${ex2.message}  ")
          }
        }


    }



    private suspend fun setAnyOtherUserToActive(phoneNumber: String){

           val listOfUsers = userRepository.getAllUserAccountsExcept(phoneNumber.trim())
           val firstUser = listOfUsers[0]
           val newUserInControl = UserModel(firstUser.MoMoName,
                                            firstUser.MoMoNumber,
                                             firstUser.AgentEmail,
                                             firstUser.AgentPassword,
                                             IsIncontrol = true,
                                              firstUser.IsRemoteRegistered,
                                                firstUser.FireBaseVerificationId)

           userRepository.updateUser(newUserInControl)
    }

    private suspend  fun setAllOtherUsersToInactive(phoneNumber: String) {
        val listOfUsers = userRepository.getAllUserAccountsExcept(phoneNumber.trim())
        listOfUsers.forEach { firstUser ->
            val tempUser =  UserModel(firstUser.MoMoName,
                firstUser.MoMoNumber,
                firstUser.AgentEmail,
                firstUser.AgentPassword,
                IsIncontrol = false,
                firstUser.IsRemoteRegistered,
                firstUser.FireBaseVerificationId)
            userRepository.updateUser(tempUser)

        }

    }


    private suspend fun setUserIncontrol(firstUser: UserModel) {
        val tempUser =  UserModel(firstUser.MoMoName,
            firstUser.MoMoNumber,
            firstUser.AgentEmail,
            firstUser.AgentPassword,
            IsIncontrol = true,
            firstUser.IsRemoteRegistered,
            firstUser.FireBaseVerificationId)

        userRepository.updateUser(tempUser)


    }

    fun deleteAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteAllUsers()
        }
    }

   private  fun setActiveUsers() {
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
    private fun getPsuedoActiveUser() {
        viewModelScope.launch {
            var activeUsers = userRepository.getActiveUser()
            if (activeUsers == null)
                _userInControl.postValue(activeUsers)
            else {
                activeUsers = userRepository.getAllUserAccounts()
                _userInControl.postValue(activeUsers)
            }
        }
    }


    @SuppressLint("LogNotTimber")
    fun registerUserWithPhoneNumber(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {

            try {
                val user = getUserWithPhoneNumber(phoneNumber)
                registerUserInFireBase(user, activity)
            } catch (ex: Exception) {
                Log.d("registerUser", "Failed due to : ${ex.message}")
            }
        }

    }

    fun registerUserInBackEndDB(request: UserRegistrationRequest?, activity: Activity) {
    viewModelScope.launch {

        val response = backEndUserRepositoryImpl.addUser(request)
        Toast.makeText(activity.applicationContext,response.body().toString(), Toast.LENGTH_LONG).show()
        Timber.d("add User To  Local DB -> ${response.code()}")
    }
    }


    fun authenticateUserInBackEndDB(request:AuthenticationRequest, activity: Activity) {
        viewModelScope.launch {
            val response = backEndUserRepositoryImpl.authenticateUser(request)
            Toast.makeText(activity.applicationContext,response.message(), Toast.LENGTH_LONG).show()
        }
    }


        @SuppressLint("LogNotTimber")
    private suspend fun registerUserInFireBase(user: UserModel, activity: Activity)  {
        registerUserInFirebaseUseCase(user, activity).collect {

            when (it) {
                is Resource.Loading -> {
                    _registrationStateTobeUsedOnMainThread.postValue(FireBaseRegistrationState.Loading)
                    Toast.makeText(activity.applicationContext, "Registration  begun", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("LoadFirebaseRegViewM", "LOADING")
                }
                is Resource.Success -> {

                    _registrationStateTobeUsedOnMainThread.postValue(FireBaseRegistrationState.Success)
                    Toast.makeText(activity.applicationContext, "Registered", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("SuccessFirebaseRegViewM", "Successful Registraton :${it.message}" )
                }
                is Resource.Error -> {
                    _registrationStateTobeUsedOnMainThread.postValue(FireBaseRegistrationState.Error(it.message))
                    Log.d("ErrorFirebaseRegViewM", "ErrorInRegistration because $it.message")
                }
            }
        }


    }


    @SuppressLint("LogNotTimber")
    fun signInUserIn(sms: String, phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            try {
                val user = getUserWithPhoneNumber(phoneNumber)
                signInUserInFireBase(user, sms, activity)
            } catch (ex: Exception) {
                Log.d("signInUser", "Failed due to : ${ex.message}")
            }
        }
    }

    @SuppressLint("LogNotTimber")
    private suspend fun signInUserInFireBase(user: UserModel, otp: String, activity: Activity) {

        signInUserInFirebaseUseCase(user, otp, activity).collect {
            when (it) {
                is Resource.Loading -> {
                    _signInStateTobeUsedOnMainThread.postValue(FireBaseRegistrationState.Loading)
                    Toast.makeText(activity.applicationContext, "SignIn begun", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("LoadFirebaseSignInViewM", "LOADING")
                }
                is Resource.Success -> {
                    Toast.makeText(activity.applicationContext, "Signed In", Toast.LENGTH_SHORT)
                        .show()
                    _signInStateTobeUsedOnMainThread.postValue(FireBaseRegistrationState.Success)
                    Log.d("SuccessFirebaseSignInViewM", "Successful Registraton")
                }
                is Resource.Error -> {
                    _signInStateTobeUsedOnMainThread.postValue(FireBaseRegistrationState.Error(it.message))
                    Toast.makeText(activity.applicationContext, "${it.message}", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("ErrorFirebaseSignInViewM", "ErrorInRegistration because $it.message")
                }
            }
        }
    }


    /*******************************************************************
     * getUserWithPhoneNumber - gets User with  a specific phone number
     *                           from the database
     *@param  phoneNumber: String
     *@return user : UserModel
     ********************************************************************/
    private suspend fun getUserWithPhoneNumber(phoneNumber: String): UserModel {
        return userRepository.getUserWithPhone(phoneNumber)
    }


    inline fun <Result> Flow<ConnectivityObserver.Status>.map(
        crossinline onUnavailable: suspend () -> Result,
        crossinline onAvailable: suspend () -> Result,
    ): Flow<Result> = map { status ->
        when (status) {
            ConnectivityObserver.Status.Unavailable -> onUnavailable()
            ConnectivityObserver.Status.Available -> onAvailable()
            ConnectivityObserver.Status.Losing -> onUnavailable()
            ConnectivityObserver.Status.Lost -> onUnavailable()
        }
    }
}