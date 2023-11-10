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


/*************************************************************************
 * Looks for backup file in external Storage location "MOMOBooklet"
 *       if no matching file is found alert user
 *                 to provide different file location in which to look
 *                  or abort whole process
 *
 *---------------------------------------------------------------
 *
 * if file is found increment database version
 *         (copy database file to  location specified in MIGRATION PATH)
 ************************************************************************/
class RecoverDatabaseDefaultUseCase
@Inject constructor(private val application: Application){

    @Suppress("DEPRECATION")
    @SuppressLint("LogNotTimber")
    operator fun invoke(): Flow<BackUpState> = flow {

        emit(BackUpState.Loading)
        try {
            clearZipFilesInCache()
        }
        catch (e:Exception){
            Log.d("Clear Cache Failure", "${e.message}")
            emit(BackUpState.Error(e.message))
        }

        try{

            val file = createRecoveryFile()
            emit(BackUpState.Success(file))
        }
        catch (ex: Exception)
        {
            Log.d("Recovery File Copying  Failure", "${ex.message}")
            emit(BackUpState.Error(ex.message))
        }
    }


    /***************************************************************************
    *  copies zip zile from External Directory
    *     to cache
    *********************************************************************************/
    @SuppressLint("LogNotTimber")
    private fun createRecoveryFile() :File
    {
        val backupFile = getBackUpFile()
        val recoveryFile = File(application.cacheDir,Constants.RECOVERY_FILENAME)
        val fis = FileInputStream(backupFile)
        val fos = FileOutputStream(recoveryFile)
        val buf = ByteArray(16384)


        try{

            var length: Int
            while (fis.read(buf).also { length = it } > 0) {
                fos.write(buf, 0, length)
            }
            fis.close()
            fos.close()
        } catch (ex:Exception)
        {
            Log.d("Failed to copy recovery file from EXTERNAL STORAGE", "${ex.message}")
        }

        return recoveryFile
    }

    private fun getBackUpFile(): File {
        val hostPath: String = Environment.getExternalStorageDirectory().path
            .plus("/").plus(application.getString(R.string.app_name))

        val backUpDirectory =
            File("$hostPath/BACKUPS")

        return File(backUpDirectory, Constants.BACKUP_FILENAME)
    }


    private  fun clearZipFilesInCache()
    {
         val cacheDir = application.cacheDir
        deleteZipsInDir(cacheDir)

    }

    private fun deleteZipsInDir(baseDir: File) {
        if (baseDir.listFiles() != null) {
            for (file in baseDir.listFiles()) {
                if (file.isDirectory) {
                    deleteZipsInDir(file)
                }
                if(file.name.contains(".zip"))
                    file.delete()
            }
        }
    }
}