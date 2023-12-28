package com.free.momobooklet_by_sm.domain.workers.remote.transactions

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.transaction.TransactionRequest
import com.free.momobooklet_by_sm.data.dto.transaction.UploadTransactionsDto
import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.free.momobooklet_by_sm.domain.use_cases.manage_transactions.UploadTransactionsUseCase
import com.free.momobooklet_by_sm.domain.use_cases.manage_users.AuthenticateUserInBackEndUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.inject.Inject

class UploadTransactionsWorker
@Inject constructor(
    private val uploadTransactionsUseCase: UploadTransactionsUseCase,
    private  val authenticateUserInBackEndUseCase: AuthenticateUserInBackEndUseCase,
    private  val userRepository: UserRepository,
    val appContext: Context,
    params: WorkerParameters
) :
    CoroutineWorker(appContext,params) {
    /************************************************************************************
     * gets list of all users
     *           gets List<TransactionRequest>  from export cache files
     *           uploads all transactions to server
     *           deletes all export cache files
     *
     *************************************************************************************/
    override suspend fun doWork(): Result {
        val listOfUsers = userRepository.getAllUserAccounts()
        val listOfListOfTransaction : MutableList<List<TransactionRequest>> = ArrayList()

        return  withContext(Dispatchers.IO) {
            try {


                //gets all transactions from different files
                listOfUsers.forEach {
                    listOfListOfTransaction.add(getTransactions(it.MoMoNumber))
                }

                //authenticates all users  in server

                listOfUsers.forEach {
                    val request = AuthenticationRequest(
                        username = it.MoMoNumber,
                        password = it.AgentPassword)

                    authenticateUserInBackEndUseCase(request).collect{ resource ->
                        when(resource){
                             is Resource.Error -> {
                                 Result.failure()
                             }
                            else -> {

                            }
                        }
                    }
                }


                // uploads all transactions to server
                listOfListOfTransaction.forEach { mite ->
                   mite.forEach { request ->
                        uploadTransactionsUseCase(request)
                            .collect{
                                when(it)
                                {
                                    is Resource.Error ->{
                                        Result.failure()
                                    }
                                    else -> {

                                    }
                                }
                            }
                    }

                }
                 Result.success()
            } catch (e: Exception) {

                Timber.d("Upload transactions worker failed ${e.message}")
                Result.failure()
            }
        }
    }



    /*****************************************************************************
     * reads cache file  and obtains  List<TransactionRequest>
     *       from json in file
     *@param moMoNumber , filename = momoNumber +"Exp.json"
     **************************************************************************/
   @Throws(Exception::class)
    private fun getTransactions(moMoNumber: String): List<TransactionRequest> {
        val finalList : MutableList<TransactionRequest> = ArrayList()

        val filename = moMoNumber.plus("Exp.json")
        val file = File(appContext.cacheDir, filename)
        try {


            val fis = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(fis))

            val gson = Gson()
            val listType = object : TypeToken<UploadTransactionsDto>() {}.type
            val transactions: UploadTransactionsDto = gson.fromJson(reader, listType)

            transactions.forEach {
                finalList.add(it.createTransactionRequest())
            }


        }catch (e:Exception)
        {
            throw Exception("failed to read  file Exp=> ${e.message}")
        }
        return finalList
    }


}