package com.example.momobooklet_by_sm.presentation.ui

import androidx.room.Transaction

sealed class ViewState {
        object Loading : ViewState()
        object Empty : ViewState()
        data class Success(val transaction: List<Transaction>) : ViewState()
        data class Error(val exception: Throwable) : ViewState()
    }

