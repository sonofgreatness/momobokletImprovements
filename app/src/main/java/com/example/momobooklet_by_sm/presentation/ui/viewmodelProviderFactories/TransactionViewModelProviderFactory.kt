package com.example.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.TransactionViewModel

class TransactionViewModelProviderFactory(
    val app: Application,
    val activity: Activity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TransactionViewModel(app,activity) as T
    }
}