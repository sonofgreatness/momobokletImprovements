package com.free.momobooklet_by_sm.domain.repositories.backup

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.QueryMap
import java.io.File


interface BackupBackEndRepository {

    suspend  fun download(
        @QueryMap params: Map<String, String>,
        @Header("Authorization") token: String

    ): Response<ResponseBody>

    suspend  fun upload(
        @Part("file") file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("/api/v1/backup/getAll")
  suspend  fun getListOfbackups(@Header("Authorization") token: String): Response<ResponseBody>


}