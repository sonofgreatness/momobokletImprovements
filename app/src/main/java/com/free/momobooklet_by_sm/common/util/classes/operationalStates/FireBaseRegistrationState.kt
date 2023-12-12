package com.free.momobooklet_by_sm.common.util.classes.operationalStates

sealed
class FireBaseRegistrationState {
    object Loading : FireBaseRegistrationState()
    object Success : FireBaseRegistrationState()
    data class Error(val exception_message: String? = null) : FireBaseRegistrationState()


}