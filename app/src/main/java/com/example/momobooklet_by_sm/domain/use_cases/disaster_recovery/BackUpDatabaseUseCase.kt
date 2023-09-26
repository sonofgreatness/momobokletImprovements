package com.example.momobooklet_by_sm.domain.use_cases.disaster_recovery

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.example.momobooklet_by_sm.common.util.Constants
import com.example.momobooklet_by_sm.common.util.Constants.Companion.BACKUP_FILENAME
import com.example.momobooklet_by_sm.common.util.Constants.Companion.ZIP_PREFIX_EXTERNAL
import com.example.momobooklet_by_sm.common.util.Constants.Companion.ZIP_PREFIX_FILES
import com.example.momobooklet_by_sm.data.local.models.TransactionModel
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

/*****************************************************************************
 * Copy database file to cache
 *  access cached version of database BACKUPDATABASE_FILE
 *  compress that file BACKUPDATABASE_FILE -> BACKUPDATABASE_FILE_DATED
 *
 * Traverse  [EXTERNAL_STORAGE_DIRECTORY]
 *        if NOT EXIST , create it
 *
 * copy/write BACKUPDATABASE_FILE_DATED  to [EXTERNAL_STORAGE_DIRECTORY]
 **************************************************************************/
class BackUpDatabase
   @Inject constructor(
                    val application: Application) {



    /***********************************************************************
     * buildBackup() is responsible for creating a file that contains our desired dataset and
     *         returning the File object pointing to that file:
     *@return File Object (in app's cache folder )
     **********************************************************************/
    private fun buildBackUp() : File?{
        val zipFile = File(application?.cacheDir, Constants.BACKUP_FILENAME)
        if (zipFile.exists()) {
            zipFile.delete()
        }
        val fos = FileOutputStream(zipFile)

        val zos = ZipOutputStream(fos)
        zos.putNextEntry(ZipEntry(""))
        Toast.makeText(application.applicationContext,"ZIP", Toast.LENGTH_LONG).show()
        application?.filesDir?.let {
            zipDir(Constants.ZIP_PREFIX_FILES, it, zos) }


        zipDirForDatabaseFiles(Constants.ZIP_PREFIX_DATABASES, getDatabasesDir(application.applicationContext), zos, listOf("user_database.db"))
        zipDir(Constants.ZIP_PREFIX_PREFS, getSharedPrefsDir(application.applicationContext), zos)

        zos.flush()
        fos.getFD().sync()
        zos.close()
        return zipFile
    }


    /******************************************************************************************
     *  zipDir() not only takes the File of data to be backed up and a ZipOutputStream
     * representing where to package the data, but it also takes a path prefix. ZIP files do
    *  not really have a directory structure; that structure is faked based on path-style
    * names associated with each entry.
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
                    }
                }
            }
        }
    }


    private  fun getSharedPrefsDir(ctxt: Context): File {
        return File(
            File(ctxt.applicationInfo.dataDir),
            "shared_prefs"
        )
    }


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
    private  fun createBackUpTransactionsFile(sourceList : List<TransactionModel>)
    {

        val converters = Gson()
        val jsonObject =converters.toJson(sourceList)

    }
    /**********************************************************************
     *Creates a Json Files From source List<UserModel>
     *@param sourceList ,   current List of User Accounts in database
     *@return Returns A .json file   of source list
     ***********************************************************************/
    private  fun createBackUpUsersFile(sourceList : List<TransactionModel>)
    {

    }

}