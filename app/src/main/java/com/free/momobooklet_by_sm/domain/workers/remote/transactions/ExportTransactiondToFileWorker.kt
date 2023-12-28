package com.free.momobooklet_by_sm.domain.workers.remote.transactions

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.util.*
import javax.inject.Inject


/********************************************************************
 *Exports transactions to a file in cache
 * `updating a database while using it  for
 *     other tasks is not wise`
 ********************************************************************/
class ExportTransactiondToFileWorker
@Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    val appContext: Context,
    params: WorkerParameters
) :
    CoroutineWorker(appContext,params) {

    /*******************************************************************
     *gets list of users
     *    for each user :
     *         gets newest transactions  from room db
     *         exports transactions to file named `usernameExp.json`
     ***********************************************************************/
    override suspend fun doWork(): Result {
        val listOfUsers = userRepository.getAllUserAccounts()
        val listOfListOfTransaction : MutableList<List<TransactionModel>> = ArrayList()

        try {


            listOfUsers.forEach {
                listOfListOfTransaction.add(
                    getNewestTransactions(it.MoMoNumber)
                )

            }
            listOfListOfTransaction.forEach {
                writeTransactionsToFile(it)
            }
        }
        catch (e:Exception){
            return  Result.failure()
        }
        return Result.success()

    }

    /*************************************************************************
     * writes list<TransactionModel> to file
     *
     *************************************************************************/
    @Throws(Exception::class)
    private fun writeTransactionsToFile(transactions: List<TransactionModel>) {

        val username = transactions[0].AgentPhoneNumber
        val filename = username.plus("Exp.json")
        val file = File(appContext.cacheDir, filename)

        if (file.exists())
            file.delete()
        file.createNewFile()

      try{
          val fileWriter = FileWriter(file)
          fileWriter.write(
              convertListToJson(transactions))

          fileWriter.close()
      }
      catch (e :Exception){
       throw e
      }

    }

    private fun convertListToJson(transactions: List<TransactionModel>): String? {
        val gson = Gson()
        return gson.toJson(transactions)
    }

    /******************************
     * gets newest Transactions by reading sharedPrefs
     *                  file to obtain  startdate  and using now as end date
     *@param moMoNumber  : AgentPhoneNumber of all transactions to be gotten
     *@return List<TransactionModel>
     ***/
    private suspend fun getNewestTransactions(moMoNumber: String) : List<TransactionModel> {

        val startDate = getStartdate(moMoNumber)
        val endDate = Date(System.currentTimeMillis()).time
        return if (startDate != 0L) {
            updateStarDate(endDate,moMoNumber)
            transactionRepository.getNewestTransactions(startDate, endDate, moMoNumber)
        } else {
            updateStarDate(endDate,moMoNumber)
            transactionRepository.getAllTransactionsRegularData(moMoNumber)
        }
    }


    /**
     * reads
     ***/
    private fun getStartdate(moMoNumber: String): Long {
        val sharedPreference =  appContext.getSharedPreferences(Constants.TRANSACTIONS_EXPORT_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreference.getLong(moMoNumber,0L)
    }

    private fun updateStarDate(endDate: Long, moMoNumber: String) {
        val sharedPreference =  appContext.getSharedPreferences(Constants.TRANSACTIONS_EXPORT_PREFS_NAME, Context.MODE_PRIVATE)
         sharedPreference
             .edit()
             .putLong(moMoNumber,endDate)
             .apply()
    }
}