package com.example.momobooklet_by_sm.database.remote

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.momobooklet_by_sm.api.Transactions
import com.example.momobooklet_by_sm.database.local.Database
import com.example.momobooklet_by_sm.database.local.models.TransactionModel
import com.example.momobooklet_by_sm.database.local.repositories.TransactionRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader



/*********************************************************
 * This Worker reads a json file 'transactions.json'
 *  and parses the json  saved  in that files into  a
 *  a  List<TransactionModel>, The  models of the list
 *  are then inserted  into the Room DB
 *********************************************************/

class ImportToRoomWorker(val appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params)  {
    /********************************************************
     *NB : you are in a Coroutine and File Access can be
     *      done on the "MainThread"
     *      so can database Access
     *********************************************************/
    override suspend fun doWork(): Result {
        /*-----------------------------------------------
         *1 Read File in cache : cache/transactions.json
         *   if file Not found return result failure
         *2.Open Inputstream: get data from file as
         *3.Parse json from file to List<Transactions> => List<TransactionModel>
         -------------------------------------------------*/
        val  gson =Gson()
        val listType = object : TypeToken<List<Transactions>>(){}.type
        val arrayListofTransactions: List<Transactions>
        val listofTransactions: MutableList<TransactionModel> by lazy {
            ArrayList()
        }
        val transactionDao = Database.getDatabase(appContext).getDao()
        val repository =  transactionDao.let { TransactionRepository(it)}

        try {
            val file = File(appContext.cacheDir, "transactions.json")
            // open inputstream
            val is_ = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(is_))
            arrayListofTransactions = gson.fromJson(reader, listType)

            Log.d("Transactions", "$arrayListofTransactions")
            Log.d("Transactions1", "${arrayListofTransactions[0].toString()}")
            Log.d("TransactionsSize", "${arrayListofTransactions.size}")
        }
        catch (e:Exception){
            Log.d("ImportToRoomTag", "could'nt parse json file : ${e.message}")
            return Result.failure()
        }
        Log.d("List of Transactions1 : ", " BeforeSIZE${listofTransactions.size}")
        for (transaction in arrayListofTransactions )
            listofTransactions.add(transaction.makeTransactionModel())
        try {

            for (real_transaction in listofTransactions)
                      repository.addTransaction(real_transaction)
            Log.d("DeleteMe1", "Reached!")
        }catch (ex: Exception)
        {
            Log.d("Failure To insert Records to Local Storge", "${ex.message}")
        }
        return  Result.success()
    }
    }
