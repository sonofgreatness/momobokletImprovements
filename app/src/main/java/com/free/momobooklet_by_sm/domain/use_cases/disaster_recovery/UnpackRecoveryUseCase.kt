package com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.ZipUtils
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.BackUpState
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.data.local.models.UserModel
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.free.momobooklet_by_sm.data.dto.BackUpTransactionModelDTOItem
import com.free.momobooklet_by_sm.data.dto.UserModelDTOItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.inject.Inject

class UnpackRecoveryUseCase
@Inject constructor(private val application: Application,
                    private val userRepository: UserRepository,
                    private  val transactionRepository: TransactionRepository)
{
    @SuppressLint("LogNotTimber")
    operator fun invoke(): Flow<BackUpState> = flow{

        emit(BackUpState.Loading)
        try{

            val recoveryFile = File(application.cacheDir, Constants.RECOVERY_FILENAME)
            val restoreFolder = File(application.cacheDir,Constants.ZIP_PREFIX_BACKUPS)

            restoreFiles(recoveryFile, restoreFolder)
            restoreDatabaseData(restoreFolder)


            emit(BackUpState.Success(recoveryFile))
        }
        catch(ex:Exception)
        {
            Log.d("Restoring Files  Failure", "${ex.message}")
            emit(BackUpState.Error(ex.message))
        }
    }


    /********************************************************************
     * Updates database using JSON files from  a specified dir
     * @param restoreFolder  the Directory that contains the Json files
     *******************************************************************/
    private suspend  fun restoreDatabaseData(restoreFolder: File){

        val  transactionsJsonFile = File(restoreFolder, Constants.BACKUP_TRANSACTIONS_FILENAME)
        val usersJsonFile = File(restoreFolder, Constants.BACKUP_USERS_FILENAME)
        updateTransactionsInDB(transactionsJsonFile)
        updateUsersInDB(usersJsonFile)
        clearBackUpFilesInCache()

    }
    /****************************************************************************
     * obtains List<TransactionModel> from a json file
     *        adds  all elements of that list  to DB table 'RECORD_SHEET'
     * @param transactionsJsonFile
     ****************************************************************************/
    @SuppressLint("LogNotTimber")
    private suspend fun updateTransactionsInDB(transactionsJsonFile: File) {
        //get Array of transactins

        val gson = Gson()
        val listType = object : TypeToken<List<BackUpTransactionModelDTOItem>>(){}.type
        val fis = FileInputStream(transactionsJsonFile)
        val reader = BufferedReader(InputStreamReader(fis))

        val listofTransactions: MutableList<TransactionModel> by lazy {
            ArrayList()
          }
        val   arrayListofTransactions : List<BackUpTransactionModelDTOItem> = gson.fromJson(reader,listType)

        for (transaction in arrayListofTransactions )
            listofTransactions.add(transaction.makeTransactionModel())
        try {
            for (real_transaction in listofTransactions)
                transactionRepository.addTransaction(real_transaction)
        }catch (ex: Exception) {

        Log.d("Failed to add transactions to DB", "${ex.message}")
        }
        fis.close()
    }


    /****************************************************************************
     * obtains List<UserModel> from a json file
     *        adds  all elements of that list  to DB table 'RECORD_SHEET'
     * @param usersJsonFile
     ****************************************************************************/
    private suspend fun updateUsersInDB(usersJsonFile: File) {
        Log.d("update user called", "update user called")
        val gson = Gson()
        val listType = object : TypeToken<List<UserModelDTOItem>>(){}.type
        val fis = FileInputStream(usersJsonFile)
        val reader = BufferedReader(InputStreamReader(fis))

        val listOfUsers : MutableList<UserModel> by lazy {
            ArrayList()
        }
        val   arrayListofUsers : List<UserModelDTOItem> = gson.fromJson(reader,listType)


        for (transaction in arrayListofUsers)
           listOfUsers.add(transaction.makeUserModel())


        try {

            for (real_user in listOfUsers)
                 userRepository.addUser(real_user)

            Log.d("update user called2", "{$listOfUsers[0]}")
        }catch (ex: Exception) {

            Log.d("Failed to add user to DB", "${ex.message}")
        }
        fis.close()

    }

    /***********************************************************************
     * Unzips zip file to appropriate directories
     * @param recoveryFile  the zip file  to be unzipped
     * @param restoreFolder directory in which to unzip database_data files
     *                          from zip file
     ***********************************************************************/
    private fun restoreFiles(recoveryFile: File, restoreFolder: File) {
        //Restore FilesDir
        com.free.momobooklet_by_sm.common.util.classes.ZipUtils.unzip(
            recoveryFile, application.filesDir,
            Constants.ZIP_PREFIX_FILES
        )

        //Restore Database File
        com.free.momobooklet_by_sm.common.util.classes.ZipUtils.unzip(
            recoveryFile, getDatabasesDir(application.applicationContext),
            Constants.ZIP_PREFIX_DATABASES
        )

        //extract transaction and  user data files
        com.free.momobooklet_by_sm.common.util.classes.ZipUtils.unzip(
            recoveryFile, restoreFolder,
            Constants.ZIP_PREFIX_DATABASEJSONFILES
        )
    }


    private  fun getDatabasesDir(ctxt: Context): File {
        return File(
            File(ctxt.applicationInfo.dataDir),
            "databases"
        )
    }


    private  fun clearBackUpFilesInCache()
    {
        val backUpDir = File(application.cacheDir,Constants.ZIP_PREFIX_BACKUPS)
        deleteFilesInDir(backUpDir)

    }

    private fun deleteFilesInDir(baseDir: File) {
        if (baseDir.listFiles() != null) {
            for (file in baseDir.listFiles()) {
                if (file.isDirectory) {
                    deleteFilesInDir(file)
                }
                file.delete()
            }
        }
    }
}