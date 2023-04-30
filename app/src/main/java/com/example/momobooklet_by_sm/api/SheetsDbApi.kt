package com.example.momobooklet_by_sm.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface  SheetsDbApi {
    @GET("exec")
    suspend fun getTransactions(@QueryMap params :Map<String, String>):Response<ResponseBody>
    @POST("exec")
    suspend fun  uploadTransactions(@QueryMap params :Map<String, String>):Response<ResponseBody>
}