package com.free.momobooklet_by_sm.presentation.ui.viewmodelProviderFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery.*
import com.free.momobooklet_by_sm.presentation.ui.viewmodels.BackupDataBaseViewModel
import javax.inject.Inject

class BackupDataBaseViewModelProviderFactory
@Inject constructor(
   private val backUpdataUseCase: BackUpDatabaseUseCase,
    private val unmountBackUpUseCase: UnmountBackUpUseCase,
    private val recoverDatabaseDefaultOption: RecoverDatabaseDefaultUseCase,
   private val unpackRecoveryUseCase: UnpackRecoveryUseCase,
   private val recoverDatabaseCustomUseCase: RecoverDatabaseCustomUseCase

) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  BackupDataBaseViewModel(backUpdataUseCase,unmountBackUpUseCase,
                                           recoverDatabaseDefaultOption,unpackRecoveryUseCase,recoverDatabaseCustomUseCase) as T
    }

}