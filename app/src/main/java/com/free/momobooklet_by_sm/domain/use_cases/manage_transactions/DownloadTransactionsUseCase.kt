package com.free.momobooklet_by_sm.domain.use_cases.manage_transactions

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.AuthTokenDto
import com.free.momobooklet_by_sm.data.dto.transaction.DownloadTransactionDto
import com.free.momobooklet_by_sm.data.dto.transaction.DownloadTransactionDtoItem
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.transaction.TransactionBackEndRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class DownloadTransactionsUseCase @Inject constructor(
    val repository: TransactionBackEndRepository,
    val application: Application,
    val localRepository: TransactionRepository
)
{

    operator fun invoke(username: String) : Flow<Resource<String>> = flow{

        try{
            emit(Resource.Loading("Get transactions Loading"))
            val accessToken = getAccessTokenFromSharedPrefs(username)
            if (accessToken != null) {
                val response = repository.getAllTransactions("Bearer $accessToken")
                if (response?.isSuccessful == true) {
                writeTransactionstoDb(response.body()?.string())
                        Timber.d("Download Transactions  ==>"+response.body()?.string())
                    emit(Resource.Success(Constants.BACKEND_TRANSACT_GET_OK))

                } else
                    emit(
                        Resource.Error("error code = "+response?.code().toString()
                        +"\n $accessToken"))
            }
            else
            {
                emit(Resource.Error("Access Token null  -> "+ Constants.BACKEND_TRANSACT_GET_FAIL))

            }

        }
        catch(ex: Exception){
            emit(Resource.Error("Exception"+ Constants.BACKEND_TRANSACT_GET_FAIL))
            Timber.d("Exception"+ Constants.BACKEND_REG_FAIL +ex.message)
        }
    }

    /**
     * converts  Json response to List<Transaction>
     *     then adds transactions to local db
     **/

    private suspend fun writeTransactionstoDb(actualResponse: String?) {
        val gson  = Gson()
        val listType = object : TypeToken<DownloadTransactionDto>(){}.type
        val   transactions : DownloadTransactionDto = gson.fromJson(actualResponse,listType)

        transactions.forEach {
          Timber.d ("Iterating through  ${it.createTransaction().Transaction_ID}")
            localRepository.addTransaction(it.createTransaction())
        }

    }

    private fun getAccessTokenFromSharedPrefs(username: String): String? {
        val sharedPreference =  application.getSharedPreferences(Constants.AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreference.getString(username,"defaultName")
    }

}