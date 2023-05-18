package com.example.momobooklet_by_sm.domain.repositories

import okhttp3.ResponseBody
import retrofit2.Response

interface RemoteTransactionsRepository {


    suspend fun getTransactions(params :Map<String, String>): Response<ResponseBody>?
    suspend fun  uploadTransactions(params :Map<String, String>): Response<ResponseBody>?

}