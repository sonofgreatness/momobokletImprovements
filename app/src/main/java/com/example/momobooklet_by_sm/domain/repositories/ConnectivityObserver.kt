package com.example.momobooklet_by_sm.domain.repositories

import kotlinx.coroutines.flow.Flow

interface  ConnectivityObserver {

fun observe() : Flow<Status>


     enum class Status
     {
         Available, Unavailable, Losing , Lost
     }
}