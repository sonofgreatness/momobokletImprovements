package com.free.momobooklet_by_sm.common.util.classes.operationalStates

sealed class BackEndRegistrationState {
    object Loading :BackEndRegistrationState()
    object Success : BackEndRegistrationState()
    data class Error(val exception_message: String? = null) : BackEndRegistrationState()
}