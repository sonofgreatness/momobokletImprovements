package com.free.momobooklet_by_sm.data.remote.repositories

import com.free.momobooklet_by_sm.data.dto.transaction.TransactionRequest
import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File


interface BackEndApi {

/*==========================================================
                            USER RELATED
==============================================================
**/

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: UserRegistrationRequest?
    ): Response<ResponseBody?>

    @POST("api/v1/auth/authenticate")
    suspend fun authenticate(
        @Body request: AuthenticationRequest?
    ): Response<ResponseBody>

    /*==========================================================
                 TRANSACTION  RELATED
    ==============================================================
    **/
    @POST("/api/v1/transact/register")
    @Throws(Exception::class)
    suspend  fun addTransaction(
        @Body request: TransactionRequest?,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("/api/v1/transact/all")
    suspend  fun getAllTransactions(
        @Header("Authorization") token: String
    ): Response<ResponseBody>?


/*==========================================================
            BACKUP  RELATED
============================================================
**/

    @GET("/api/v1/backup/download")
    suspend  fun download(
        @QueryMap params: Map<String, String>,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @Multipart
    @POST("/api/v1/backup/upload")
    suspend  fun upload(
        @Part file:MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("/api/v1/backup/getAll")
   suspend fun getListOfbackups(@Header("Authorization") token: String): Response<ResponseBody>

}