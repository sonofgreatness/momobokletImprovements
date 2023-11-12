package com.free.momobooklet_by_sm.data.remote.repositories.user

import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
import com.free.momobooklet_by_sm.data.remote.repositories.BackEndApi
import com.free.momobooklet_by_sm.domain.repositories.user.BackEndUserRepository
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class BackEndUserRepositoryImpl @Inject constructor(private val api: BackEndApi): BackEndUserRepository {


    override suspend fun addUser(request: UserRegistrationRequest?): Response<ResponseBody?> =
         api.register(request)

    override suspend fun authenticateUser(request: AuthenticationRequest?): Response<ResponseBody> =
        api.authenticate(request)

}