package com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.Constants.Companion.ZIP_PREFIX_FILES
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.BackUpState
import com.free.momobooklet_by_sm.data.local.models.TransactionModel
import com.free.momobooklet_by_sm.data.local.models.UserModel
import com.free.momobooklet_by_sm.domain.repositories.TransactionRepository
import com.free.momobooklet_by_sm.domain.repositories.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

/*****************************************************************************
 * Copy database file to cache
 *  access cached version of database BACKUPDATABASE_FILE
 *  compress that file BACKUPDATABASE_FILE -> BACKUPDATABASE_FILE_DATED
 *
 * Traverse  EXTERNAL_STORAGE_DIRECTORY
 *        if NOT EXIST , create it
 *
 * copy/write BACKUPDATABASE_FILE_DATED  to EXTERNAL_STORAGE_DIRECTORY
 **************************************************************************/

@Suppress("BlockingMethodInNonBlockingContext")
class BackUpDatabaseUseCase
   @Inject constructor(
                    val application: Application,
                    val userRepository: UserRepository,
                    val transactionRepository: TransactionRepository
                    )

{

    @SuppressLint("LogNotTimber")
    /********************************************************************
     *writes backup zip file to application  cache folder
     ********************************************************************/
        operator fun invoke() : Flow<BackUpState> = flow{

        emit(BackUpState.Loading)
          try {
             val zipFile = buildBackUp()
              emit(BackUpState.Success(zipFile))
          }
          catch (ex: Exception)
          {
              Log.d("Back Up Failure :", "${ex.message}")
              emit(BackUpState.Error(ex.message))
          }
    }


    /***********************************************************************
     * buildBackup() is responsible for creating a file that contains our desired dataset and
     *         returning the File object pointing to that file:
     *@return Zip File  Object (in app's cache folder )
     **********************************************************************/
    @SuppressLint("LogNotTimber")
     private suspend fun buildBackUp() : File{

        val zipFile = File(application.cacheDir, Constants.BACKUP_FILENAME)
        if (zipFile.exists()) {
            zipFile.delete()
        }
        val fos = FileOutputStream(zipFile)
        val zos = ZipOutputStream(fos)

        val currentUsers       =  userRepository.getAllUserAccounts()
        val currentTransactions = transactionRepository.getAllTransactionsRegularData_All()


        createBackUpUsersFile(currentUsers)
        createBackUpTransactionsFile(currentTransactions)

      try
      {
          writeToZip(zos)
      } catch (ex: Exception)
      {
          Log.d("Write to  Zip Failed ", "${ex.message}")
      }

        zos.flush()
        fos.fd.sync()
        zos.close()
        return zipFile
    }

    @SuppressLint("LogNotTimber")
    private fun writeToZip(
        zos: ZipOutputStream,
    ) {
        zos.putNextEntry(ZipEntry("delete_me.xyz"))



        //inputting report files  and  ManageStartDate.txt
        application.filesDir?.let {
            zipDir(ZIP_PREFIX_FILES, it, zos)
        }

         //inputting database file
        zipDirForDatabaseFiles(
            Constants.ZIP_PREFIX_DATABASES,
            getDatabasesDir(application.applicationContext),
            zos,
            listOf("user_database.db")
        )


        //inputting shared_prefs folder
        zipDir(Constants.ZIP_PREFIX_PREFS, getSharedPrefsDir(application.applicationContext), zos)

        // inputting transaction and user data json files
      application.filesDir?.let {
          zipDirForDatabaseJsonFiles(
              Constants.ZIP_PREFIX_DATABASEJSONFILES,
              it,
              zos,
              listOf(Constants.BACKUP_TRANSACTIONS_FILENAME, Constants.BACKUP_USERS_FILENAME)
          )
      }
    }



    /******************************************************************************************
     *  zipDir() not only takes the File of data to be backed up and a ZipOutputStream
     * representing where to package the data, but it also takes a path prefix. ZIP files do
     * not really have a directory structure; that structure is faked based on path-style
     * names associated with each entry.
     *
     *  only saves .pdf,.csv and .txt  files
    ************************************************************************************************/
    /******************************************************************************************
     *  zipDir() not only takes the File of data to be backed up and a ZipOutputStream
     * representing where to package the data, but it also takes a path prefix. ZIP files do
     * not really have a directory structure; that structure is faked based on path-style
     * names associated with each entry.
     *
     *  only saves .pdf,.csv and .txt  files
    ************************************************************************************************/
    @Throws(IOException::class)
    private fun zipDir(
        basePath: String, dir: File,
        zos: ZipOutputStream
    ) {
        val buf = ByteArray(16384)
        if (dir.listFiles() != null) {
            for (file in dir.listFiles()) {
                if (file.isDirectory) {
                    val path = basePath + file.name + "/"
                    zos.putNextEntry(ZipEntry(path))
                    zipDir(path, file, zos)
                    zos.closeEntry()
                }
                else {


                    if(file.name.contains(".csv") ||
                        file.name.contains(".pdf")
                        || file.name.contains(".txt")
                    ) {
                        val fin = FileInputStream(file)
                        var length: Int
                        zos.putNextEntry(
                            ZipEntry(basePath + file.name)
                        )
                        while (fin.read(buf).also { length = it } > 0) {
                            zos.write(buf, 0, length)
                        }
                        zos.closeEntry()
                        fin.close()
                    }
                }
            }
        }
    }
    /******************************************************************************************
     *  zipDir() not only takes the File of data to be backed up and a ZipOutputStream
     * representing where to package the data, but it also takes a path prefix. ZIP files do
     *  not really have a directory structure; that structure is faked based on path-style
     * names associated with each entry.
     *
     * @param  databaseFiles : List<String>   , list of expected  names of database files
     *                                          to be backed up
     *********************************************************************************************/

    /******************************************************************************************
     *  zipDir() not only takes the File of data to be backed up and a ZipOutputStream
     * representing where to package the data, but it also takes a path prefix. ZIP files do
     *  not really have a directory structure; that structure is faked based on path-style
     * names associated with each entry.
     *
     * @param  databaseFiles : List<String>   , list of expected  names of database files
     *                                          to be backed up
     *********************************************************************************************/

    @Throws(IOException::class)
    private fun zipDirForDatabaseFiles(
        basePath: String,
        dir: File,
        zos: ZipOutputStream,
        databaseFiles : List<String>
    ) {
        val buf = ByteArray(16384)
        if (dir.listFiles() != null) {
            for (file in dir.listFiles()) {
                if (file.isDirectory) {
                    val path = basePath + file.name + "/"
                    zos.putNextEntry(ZipEntry(path))
                    zipDirForDatabaseFiles(path, file, zos,databaseFiles)
                    zos.closeEntry()
                }
                else {

                        val fin = FileInputStream(file)
                        var length: Int
                        zos.putNextEntry(
                            ZipEntry(basePath + file.name)
                        )

                        while (fin.read(buf).also { length = it } > 0) {
                            zos.write(buf, 0, length)
                        }
                        zos.closeEntry()
                        fin.close()

                }
            }
        }
    }


    @Throws(IOException::class)
    private fun zipDirForDatabaseJsonFiles(
        basePath: String,
        dir: File,
        zos: ZipOutputStream,
        databaseFiles : List<String>
    ) {
        val buf = ByteArray(16384)
        if (dir.listFiles() != null) {
            for (file in dir.listFiles()) {
                if (file.isDirectory) {
                    val path = basePath + file.name + "/"
                    zos.putNextEntry(ZipEntry(path))
                    zipDirForDatabaseJsonFiles(path, file, zos,databaseFiles)
                    zos.closeEntry()
                }
                else {
                    if (file.name in databaseFiles) {
                        val fin = FileInputStream(file)
                        var length: Int
                        zos.putNextEntry(
                            ZipEntry(basePath + file.name)
                        )

                        while (fin.read(buf).also { length = it } > 0) {
                            zos.write(buf, 0, length)
                        }
                        zos.closeEntry()
                        fin.close()
                        file.delete()
                    }
                }
            }
        }
    }

    /**************************************************************
    * gets  shared preferences directory
    **************************************************************/
    /**************************************************************
    * gets  shared preferences directory
    **************************************************************/
    private  fun getSharedPrefsDir(ctxt: Context): File {
        return File(
            File(ctxt.applicationInfo.dataDir),
            "shared_prefs"
        )
    }

    /**************************************************************
     * gets  databases directory
     **************************************************************/
    /**************************************************************
     * gets  databases directory
     **************************************************************/
    private  fun getDatabasesDir(ctxt: Context): File {
        return File(
            File(ctxt.applicationInfo.dataDir),
            "databases"
        )
    }

    /*************************************************************
     *Creates a Json Files From source List<TransactionModel>
     *@param sourceList ,   current List of Transactions in database
     *@return Returns A .json file   of source list
     ****************************************************************/
    /*************************************************************
     *Creates a Json Files From source List<TransactionModel>
     *@param sourceList ,   current List of Transactions in database
     *@return Returns A .json file   of source list
     ****************************************************************/
    @SuppressLint("LogNotTimber")
    fun createBackUpTransactionsFile(sourceList : List<TransactionModel>) : File
    {

        val converters = Gson()
        val jsonObject =converters.toJson(sourceList)
        val returnFile = File(application.filesDir,Constants.BACKUP_TRANSACTIONS_FILENAME)

        try{

            val writer =  FileWriter(returnFile)
            writer.write(jsonObject)
            writer.close()
        }
        catch (ex: Exception)
        {

            Log.d("Failed to Write Json to File", "${ex.message}")
        }
        return  returnFile
    }
    /**********************************************************************
     *Creates a Json Files From source List<UserModel>
     *@param sourceList ,   current List of User Accounts in database
     *@return Returns A .json file   of source list
     ***********************************************************************/
    /**********************************************************************
     *Creates a Json Files From source List<UserModel>
     *@param sourceList ,   current List of User Accounts in database
     *@return Returns A .json file   of source list
     ***********************************************************************/
    @SuppressLint("LogNotTimber")
    private  fun createBackUpUsersFile(sourceList : List<UserModel>) : File
    {

        val converters = Gson()
        val jsonObject =converters.toJson(sourceList)
        val returnFile = File(application.filesDir,Constants.BACKUP_USERS_FILENAME)

        try{

            val writer =  FileWriter(returnFile)
            writer.write(jsonObject)
            writer.close()
        }
        catch (ex: Exception)
        {
            Log.d("Failed to Write Json jj to File", "${ex.message}")
        }
        return  returnFile
    }

}