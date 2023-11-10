package com.free.momobooklet_by_sm.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.free.momobooklet_by_sm.common.util.classes.ReportType
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.ExportState
import com.free.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.WriteReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GenerateReportViewModel@Inject constructor(
    val datesManager: CommissionDatesManagerRepository,
    private val writeReportUseCase: WriteReportUseCase)
    : ViewModel(){




    fun writeReport(startDate: String, type: ReportType) {
        viewModelScope.launch {
            Timber.d("ExportState 0.0 -> EmptyMadeIt")
            writeReportUseCase(startDate, type).collect {
                when (it) {
                    is ExportState.Empty -> {
                        Timber.d("ExportState 1 -> EmptyMadeIt")
                    }
                    is ExportState.Loading -> {
                        Timber.d("ExportState 2 -> LoadingMadeIt")
                    }
                    is ExportState.Success -> {
                        Timber.d("ExportState 3 -> SuccessMadeIt")
                        Timber.d("ExportState 3... --> ${it.fileUri}")
                    }
                    is ExportState.Error -> {

                        if (it.exception_message?.contains("Operation not permitted") == true) {
                            // Request Permission  FiLeManageMent Permission Here
                            Timber.d("ExportState 4 -> ErrorMadeIt  ${it.exception_message}")
                        } else
                            Timber.d("ExportState 4 -> ErrorMadeIt doesn't contain expected IO ${it.exception_message}")
                    }
                }
            }
        }
    }
}