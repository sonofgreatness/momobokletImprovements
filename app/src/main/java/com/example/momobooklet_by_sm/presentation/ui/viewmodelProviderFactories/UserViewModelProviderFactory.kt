package com.example.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.example.momobooklet_by_sm.domain.repositories.ConnectivityObserver
import com.example.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.example.momobooklet_by_sm.domain.repositories.UserRepository
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.TransactionViewModel
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.UserViewModel

class UserViewModelProviderFactory (
    val userRepository: UserRepository,
    val connectivityObserver: ConnectivityObserver
    ) : ViewModelProvider.Factory {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(userRepository, connectivityObserver) as T
        }
    }
