package com.free.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.free.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.WriteReportUseCase
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.GenerateReportViewModel
import javax.inject.Inject

class GenerateReportViewModelProviderFactory@Inject constructor(
    val datesManager: CommissionDatesManagerRepository,
    private val writeReportUseCase: WriteReportUseCase
): ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  GenerateReportViewModel(datesManager, writeReportUseCase) as T
    }
}