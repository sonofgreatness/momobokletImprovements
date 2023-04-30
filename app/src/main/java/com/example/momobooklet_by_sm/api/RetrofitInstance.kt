package com.example.momobooklet_by_sm.api

import com.example.momobooklet_by_sm.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object{
        //by lazy{} means initialize what's in the curly braces once
        private val retrofit by lazy{
            val logging=HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()




        }
        //actualapi object used everywhere to make network requests
        val api by lazy {
            retrofit.create(SheetsDbApi::class.java)
        }

    }

}