package com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery

import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import android.util.Log
import com.free.momobooklet_by_sm.R
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.BackUpState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

/*********************************************************
 * copies  backup zip file in cache  folder
 *  to EXTERNAL DIRECTORY
 *********************************************************/
class UnmountBackUpUseCase
@Inject constructor(
    val application: Application
    ) {


    @Suppress("DEPRECATION")
    @SuppressLint("LogNotTimber")
    operator fun invoke(): Flow<BackUpState> = flow {

        emit(BackUpState.Loading)
        try {
            val file = writeBackUpFileToExternalDir()
            emit(BackUpState.Success(file))
        } catch (ex: Exception) {
            Log.d("Back Up Unmounting Failure", "${ex.message}")
            emit(BackUpState.Error(ex.message))
        }
    }

    /********************************************************
     * copies backup.zip from cache to external memory
     *  then deletes it
     ******************************************************/
    @SuppressLint("LogNotTimber")
    private fun writeBackUpFileToExternalDir(): File {

        val buf = ByteArray(16384)
        //OPEN BACK UP FILE IN CACHE
        val zipFile = File(application.cacheDir, Constants.BACKUP_FILENAME)
        val backUpDir = checkIfExternalDirExists()


        if (zipFile.exists()) {
            val fis = FileInputStream(zipFile)
            val fos = FileOutputStream(backUpDir)


            try {
                var length: Int
                while (fis.read(buf).also { length = it } > 0) {
                    fos.write(buf, 0, length)
                }
                fis.close()
                fos.close()

            } catch (ex: Exception) {
                Log.d("Failed to copy back up file to EXTERNAL STORAGE", "${ex.message}")
            }
        }
        return backUpDir
    }

    /*--------------------------------------------------
      Step 1 check  permissions
      Step 2  Check IF EXTERNAL DIR EXISTS :
             IF  NOT CREATE IT
      Step 3    Move File To EXTERNAL DIRECTORY
     ---------------------------------------------------*/
    /************************************************************************
     *Creates A Folder in external  Memory
     *       by the name  application's ("MOMOBOOKLET/BACKUPS")
     *@return  Returns backUpDirectory
     ***********************************************************************/

    private fun checkIfExternalDirExists(): File {
        val hostPath: String = Environment.getExternalStorageDirectory().path
            .plus("/").plus(application.getString(R.string.app_name))
        val rootDirectory = File(hostPath)
        if (!rootDirectory.exists())
            rootDirectory.mkdir()

        val backUpDirectory =
            File("$hostPath/BACKUPS")

        if (!backUpDirectory.exists())
            backUpDirectory.mkdir()

        val externalBackUp = File(backUpDirectory, Constants.BACKUP_FILENAME)

        if(externalBackUp.exists())
            externalBackUp.delete()

        externalBackUp.createNewFile()

        return externalBackUp
    }
}

