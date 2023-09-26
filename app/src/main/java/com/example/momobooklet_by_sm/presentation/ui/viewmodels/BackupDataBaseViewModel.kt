package com.example.momobooklet_by_sm.presentation.ui.viewmodels

import com.example.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.example.momobooklet_by_sm.domain.repositories.UserRepository
import com.example.momobooklet_by_sm.domain.use_cases.disaster_recovery.BackUpDatabaseUseCase
import com.example.momobooklet_by_sm.domain.use_cases.disaster_recovery.UnmountBackUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BackupDataBaseViewModel
@Inject constructor(private val userRepository: UserRepository,
                    private val transactionRepository: TransactionRepository,
                    private  val backUpdataUseCase: BackUpDatabaseUseCase,
                     private val unmountBackUpUseCase: UnmountBackUpUseCase)
{





}