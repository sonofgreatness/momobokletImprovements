package com.example.momobooklet_by_sm.ui.viewmodelProviderFactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.ui.viewmodels.BackupViewModel

class BackupViewModelProviderFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  BackupViewModel(application) as T
    }
}
