package com.example.momobooklet_by_sm.database.local.repositories


import com.example.momobooklet_by_sm.database.local.daos.DatabaseDao
import com.example.momobooklet_by_sm.database.local.models.BACKUP_METADATA
import com.example.momobooklet_by_sm.database.local.models.TransactionModel
import kotlinx.coroutines.flow.Flow


class TransactionRepository(private val userDao: DatabaseDao){

     fun readAllTransactiondata(): Flow<List<TransactionModel>> {
         return userDao.readAllTransactiondata()
     }

    fun searchTransactions(query: String) : Flow<List<TransactionModel>>
    {
        return userDao.searchTransactions(query)
    }

    fun  getSellTransactions():Flow<List<TransactionModel>>
    {
        return  userDao.getSellTransactions()
    }
    fun getBuyTransactions():Flow<List<TransactionModel>>
    {
        return userDao.getBuyTransactions()
    }

    fun getTodaysTransactions(date:String):Flow<List<TransactionModel>> {
        return userDao.getTodaysTransactions(date)
    }


   suspend fun getDailyTransactions(date:String): List<TransactionModel>
                                              = userDao.getDailyTransactions(date)


    suspend fun addTransaction (transaction:TransactionModel){
        userDao.addTransaction(transaction)
    }
     fun deleteAll(){
        userDao.deleteAll()
    }
    fun removeTransaction(transaction:TransactionModel){
        userDao.removeTransaction(transaction)
    }



    /*----------------------------------------------------------------------
                            BACKUP_META DATA
 -----------------------------------------------------------------------*/
    fun  clearTransactionMetaData() = userDao.clearTransactionMetaData()
    suspend fun  getTransactionMetaData():List<BACKUP_METADATA> = userDao.getTransactionMetaData()
    suspend fun addTransactionMetaData(data: BACKUP_METADATA) = userDao.addTransactionMetaData(data)

    suspend fun getAllTransactionsRegularData() = userDao.getAllTransactionsRegularData()
}