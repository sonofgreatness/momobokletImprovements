package com.free.momobooklet_by_sm.data.remote.repositories.transaction

import com.free.momobooklet_by_sm.data.dto.transaction.TransactionRequest
import com.free.momobooklet_by_sm.data.remote.repositories.BackEndApi
import com.free.momobooklet_by_sm.domain.repositories.transaction.TransactionBackEndRepository
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class TransactionBackEndRepoImpl
@Inject constructor(val api: BackEndApi) :TransactionBackEndRepository

    {
    override suspend fun addTransaction(request: TransactionRequest?, token: String):
              Response<ResponseBody> = api.addTransaction(request,toString())

    override suspend fun getAllTransactions(token: String): Response<ResponseBody>?
                = api.getAllTransactions(token)
    }