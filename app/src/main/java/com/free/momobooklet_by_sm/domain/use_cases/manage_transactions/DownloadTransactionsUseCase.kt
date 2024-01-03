package com.free.momobooklet_by_sm.domain.use_cases.manage_transactions

import android.app.Application
import android.content.Context
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.Constants.Companion.DOWNLOADED_TRANSACTIONS_FILE
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.domain.repositories.transaction.TransactionBackEndRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

class DownloadTransactionsUseCase @Inject constructor(
    val repository: TransactionBackEndRepository,
    val application: Application
    )
{

    operator fun invoke(username: String) : Flow<Resource<String>> = flow{

        try{
            emit(Resource.Loading("Get transactions Loading"))
            val accessToken = getAccessTokenFromSharedPrefs(username)
            if (accessToken != null) {
                val response = repository.getAllTransactions("Bearer $accessToken")
                if (response?.isSuccessful == true) {
                writeTransactionstoFile(response.body()?.string(),username)
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

    /*******************************************************
     * converts  Json response to List<Transaction>        *
     *     then adds transactions to file named
     *           downloadedTransactions.json
     ******************************************************/

    @Throws(Exception::class)
    private  fun writeTransactionstoFile(actualResponse: String?,username:String) {
        val file = File(application.applicationContext.cacheDir,
                               username.plus(
                                 DOWNLOADED_TRANSACTIONS_FILE))
        if (file.exists())
            file.delete()
        file.createNewFile()
        val writer = FileWriter(file)
        try {
            writer.write(actualResponse)
            writer.close()
        }catch (e:Exception)
        {
            throw e
        }


    }

    private fun getAccessTokenFromSharedPrefs(username: String): String? {
        val sharedPreference =  application.getSharedPreferences(Constants.AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreference.getString(username,"defaultName")
    }

}