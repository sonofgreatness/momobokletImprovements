package com.free.momobooklet_by_sm.data.remote.repositories

import com.free.momobooklet_by_sm.data.remote.SheetsDbApi
import com.free.momobooklet_by_sm.domain.repositories.RemoteTransactionsRepository
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class RemoteTransactionsRepositoryImpl @Inject constructor(val api: SheetsDbApi) : RemoteTransactionsRepository {


    override suspend fun getTransactions(params: Map<String, String>): Response<ResponseBody> {
        return api.getTransactions(params)
    }

    override suspend fun uploadTransactions(params: Map<String, String>): Response<ResponseBody> {
        return api.uploadTransactions(params)
    }
}