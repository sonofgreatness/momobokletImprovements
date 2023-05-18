package com.example.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import android.app.Activity
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.example.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.TransactionViewModel

class TransactionViewModelProviderFactory(
    val transactionRepository: TransactionRepository,
    val datesManager: CommissionDatesManagerRepository
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TransactionViewModel(transactionRepository,datesManager) as T
    }
}