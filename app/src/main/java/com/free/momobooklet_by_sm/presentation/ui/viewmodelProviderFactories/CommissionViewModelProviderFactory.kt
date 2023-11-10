package com.free.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.free.momobooklet_by_sm.domain.repositories.CommissionDatesManagerRepository
import com.free.momobooklet_by_sm.domain.repositories.CommissionRepository
import com.free.momobooklet_by_sm.domain.use_cases.commission.*
import com.free.momobooklet_by_sm.domain.use_cases.managefiles_use_cases.WriteReportUseCase
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.CommissionViewModel
import javax.inject.Inject

class CommissionViewModelProviderFactory @Inject constructor(
    val datesManagerRepository: CommissionDatesManagerRepository,
    val writeReportUseCase: WriteReportUseCase,
    val todayCommissionUseCase: TodayCommissionUseCase,
    val yesterdayCommissionUseCase: YesterdayCommissionUseCase,
    val customDayCommissionUseCase: CustomDayCommissionUseCase,
    val lastMonthUseCase: LastMonthUseCase,
    val thisMonthUsecase: ThisMonthUsecase,
    val commissionRepository: CommissionRepository
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(CommissionViewModel::class.java)){
           return CommissionViewModel(
                                datesManagerRepository,todayCommissionUseCase,
                                 yesterdayCommissionUseCase,customDayCommissionUseCase,
                                 lastMonthUseCase,thisMonthUsecase, commissionRepository) as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}