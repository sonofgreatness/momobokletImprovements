package com.example.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import android.app.Activity
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.example.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.example.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.example.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.WriteReportUseCase
import com.example.momobooklet_by_sm.presentation.ui.viewmodels.CommissionViewModel
import javax.inject.Inject

class CommissionViewModelProviderFactory @Inject constructor(
    val commission_repository: CommissionRepository,
    val repository: TransactionRepository,
    val datesManager: CommissionDatesManagerRepository,
     val writeReportUseCase: WriteReportUseCase
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(CommissionViewModel::class.java)){
            return CommissionViewModel(commission_repository,repository,datesManager,writeReportUseCase) as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}