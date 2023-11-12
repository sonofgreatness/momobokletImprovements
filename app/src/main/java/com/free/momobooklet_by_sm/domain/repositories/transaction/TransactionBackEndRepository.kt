package com.free.momobooklet_by_sm.domain.repositories.transaction

import com.free.momobooklet_by_sm.data.dto.transaction.TransactionRequest
import okhttp3.ResponseBody
import retrofit2.Response

interface TransactionBackEndRepository {

    suspend  fun addTransaction(request: TransactionRequest?, token:String): Response<ResponseBody>
    suspend  fun getAllTransactions(token:String): Response<ResponseBody>?
}