package com.free.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.free.momobooklet_by_sm.domain.repositories.ConnectivityObserver
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.free.momobooklet_by_sm.domain.repositories.user.BackEndUserRepository
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.AuthenticateUserInBackEndUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.RegisterUserInBackEndUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.RegisterUserInFirebaseUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.SignInUserInFirebaseUseCase
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel
import javax.inject.Inject

class UserViewModelProviderFactory @Inject constructor (
    val userRepository: UserRepository,
    val connectivityObserver: ConnectivityObserver,
     val registerUserInFirebaseUseCase: RegisterUserInFirebaseUseCase,
    val signInUserInFirebaseUseCase: SignInUserInFirebaseUseCase,
    val registerUserInBackEndUseCase: RegisterUserInBackEndUseCase,
    val authenticateUserInBackEndUseCase: AuthenticateUserInBackEndUseCase
    ) : ViewModelProvider.Factory {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(userRepository,connectivityObserver,
                registerUserInFirebaseUseCase,
                signInUserInFirebaseUseCase,
                registerUserInBackEndUseCase,
                authenticateUserInBackEndUseCase
            ) as T
        }
    }
