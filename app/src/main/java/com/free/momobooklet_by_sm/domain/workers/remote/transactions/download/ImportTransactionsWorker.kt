package com.free.momobooklet_by_sm.domain.workers.remote.transactions.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.dto.transaction.DownloadTransactionDto
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.inject.Inject

/*************************************************
 * Reads downloaded transactions from files
 *      and adds them to local db
 ********************************************/
class ImportTransactionsWorker @Inject constructor(
    private val userRepository: UserRepository,
    private val repository:  TransactionRepository,
    val appContext: Context,
    params: WorkerParameters
) :
    CoroutineWorker(appContext,params) {
    override suspend fun doWork(): Result {
       val listOfUsers = userRepository.getAllUserAccounts()

        Timber.d("import transactions called")
        try {
            listOfUsers.forEach {
                val transactions = getListOfTransactionsFromFile(it.MoMoNumber)
                Timber.d("imports list  => ${transactions.size}")
                transactions.forEach {
                    repository.addTransaction(it)
                }
            }
        }catch (e:Exception)
        {
           return  Result.failure()
        }


        return Result.success()

    }

    private fun getListOfTransactionsFromFile(username:String): List<TransactionModel> {
        val finalList : MutableList<TransactionModel> = ArrayList()
        val file = File(appContext.cacheDir, username.plus(Constants.DOWNLOADED_TRANSACTIONS_FILE))

        try {

            val fis = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(fis))

            val gson = Gson()
            val listType = object : TypeToken<DownloadTransactionDto>() {}.type
            val transactions:DownloadTransactionDto  = gson.fromJson(reader, listType)

            transactions.forEach {
                finalList.add(it.createTransaction())
            }
            fis.close()

        }catch (e:Exception)
        {
            throw Exception("failed to read  file Down=> ${e.message}")
        }
        return finalList
    }
}