package com.free.momobooklet_by_sm.common.util.classes.operationalStates

import java.io.File

sealed class BackUpState {
    object Loading : BackUpState()
    data class Success(val fileUri: File? = null) : BackUpState()
    data class Error(val exception_message: String? = null) : BackUpState()
}
