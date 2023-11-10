package com.free.momobooklet_by_sm.data.local.repositories


import com.free.momobooklet_by_sm.data.local.daos.TransactionDao
import com.free.momobooklet_by_sm.data.local.models.BACKUP_METADATA
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//Inject dao here
class TransactionRepositoryImpl @Inject constructor(private val userDao: TransactionDao) : TransactionRepository
{

     override fun readAllTransactiondata(): Flow<List<TransactionModel>> {
         return userDao.readAllTransactiondata()
     }

    override fun searchTransactions(query: String) : Flow<List<TransactionModel>>
    {
        return userDao.searchTransactions(query)
    }

    override fun  getSellTransactions():Flow<List<TransactionModel>>
    {
        return  userDao.getSellTransactions()
    }
    override fun getBuyTransactions():Flow<List<TransactionModel>>
    {
        return userDao.getBuyTransactions()
    }

    override fun getTodaysTransactions(date:String, momoNumber: String):Flow<List<TransactionModel>> {
        return userDao.getTodaysTransactions(date, momoNumber)
    }


   override suspend fun getDailyTransactions(date:String, momoNumber: String): List<TransactionModel>
                                              = userDao.getDailyTransactions(date, momoNumber)


    override suspend fun addTransaction (transaction:TransactionModel){
        userDao.addTransaction(transaction)
    }
     override fun deleteAll(){
        userDao.deleteAll()
    }
    override fun removeTransaction(transaction:TransactionModel){
        userDao.removeTransaction(transaction)
    }



    /*----------------------------------------------------------------------
                            BACKUP_META DATA
 -----------------------------------------------------------------------*/
    override fun  clearTransactionMetaData() = userDao.clearTransactionMetaData()
    override suspend fun  getTransactionMetaData():List<BACKUP_METADATA> = userDao.getTransactionMetaData()
    override suspend fun addTransactionMetaData(data: BACKUP_METADATA) = userDao.addTransactionMetaData(data)
    override suspend fun getAllTransactionsRegularData(momoNumber: String) = userDao.getAllTransactionsRegularData(momoNumber)
    override suspend fun getAllTransactionsRegularData_All(): List<TransactionModel> =
        userDao.getAllTransactionsRegularData_All()


}