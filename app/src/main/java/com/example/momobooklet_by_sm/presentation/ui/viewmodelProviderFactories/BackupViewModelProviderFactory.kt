package com.example.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.domain.repositories.RemoteTransactionsRepository
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.BackupViewModel
import javax.inject.Inject

class BackupViewModelProviderFactory @Inject constructor(val application: Application,
                                                         val remoteTransactionsRepository: RemoteTransactionsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  BackupViewModel(application,remoteTransactionsRepository) as T
    }
}
