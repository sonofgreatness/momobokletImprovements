package com.free.momobooklet_by_sm.common.util.classes.operationalStates


/*******************************************
 *Helps to monitor state of Http Requests
 *******************************************/
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}