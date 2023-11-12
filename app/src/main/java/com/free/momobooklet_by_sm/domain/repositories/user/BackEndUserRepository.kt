package com.free.momobooklet_by_sm.domain.repositories.user

import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
import okhttp3.ResponseBody
import retrofit2.Response

interface BackEndUserRepository {


    suspend fun addUser(
        request: UserRegistrationRequest?): Response<ResponseBody?>

    suspend fun authenticateUser(
         request: AuthenticationRequest?): Response<ResponseBody>
}