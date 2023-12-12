package com.free.momobooklet_by_sm.domain.use_cases.manage_transactions

import android.app.Application
import android.content.Context
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.transaction.TransactionRequest
import com.free.momobooklet_by_sm.domain.repositories.transaction.TransactionBackEndRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class uploadTransactionsUseCase
@Inject constructor(
    val repository: TransactionBackEndRepository,
    val application: Application
)
{

    operator fun invoke(request : TransactionRequest) : Flow<Resource<String>> = flow{

        try{
            emit(Resource.Loading("Registration Loading"))
            val accessToken = getAccessTokenFromSharedPrefs(request.username)
            if (accessToken != null) {
                val response = repository.addTransaction(request, accessToken)
                if (response.isSuccessful) {

                    emit(Resource.Success(Constants.BACKEND_TRANSACT_ADD_OK))

                } else
                    emit(Resource.Error("error code = "+response.code().toString()+"\n token ==>${accessToken} "+Constants.BACKEND_TRANSACT_ADD_FAIL))
            }
            else
            {
                emit(Resource.Error("Access Token null  -> "+Constants.BACKEND_TRANSACT_ADD_FAIL))

            }

        }
        catch(ex: Exception){
            emit(Resource.Error("Exception"+Constants.BACKEND_TRANSACT_ADD_FAIL))
            Timber.d("Exception"+Constants.BACKEND_REG_FAIL +ex.message)
        }
    }

    private fun getAccessTokenFromSharedPrefs(username: String): String? {
        val sharedPreference =  application.getSharedPreferences(Constants.AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreference.getString(username,"defaultName")
    }


}