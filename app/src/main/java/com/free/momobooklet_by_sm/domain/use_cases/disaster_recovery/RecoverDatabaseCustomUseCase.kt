package com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.util.Log
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.BackUpState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class RecoverDatabaseCustomUseCase
@Inject constructor(private val  application: Application)
{

    @Suppress("DEPRECATION")
    @SuppressLint("LogNotTimber")
    operator fun invoke(data : Uri): Flow<BackUpState> = flow {

        emit(BackUpState.Loading)
        try {
            clearZipFilesInCache()
        }
        catch (e:Exception){
            Log.d("Clear Cache Failure", "${e.message}")
            emit(BackUpState.Error(e.message))
        }

        try{

            val file = createRecoveryFile(data)
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
    private fun createRecoveryFile(data: Uri) : File
    {
        val backupFile = getBackUpFile(data)
        val recoveryFile = File(application.cacheDir, Constants.RECOVERY_FILENAME)
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
            Log.d("Custom Failed to copy recovery file from EXTERNAL STORAGE", "${ex.message}")
        }

        return recoveryFile
    }

    @SuppressLint("LogNotTimber")
    private fun getBackUpFile(data : Uri): File {

        val buf = ByteArray(16384)

        val backUpdir = File(application.cacheDir,Constants.BACKUP_FILENAME)
        val fos = FileOutputStream(backUpdir)
        val fis : InputStream? = application.contentResolver.openInputStream(data)

        var length: Int


            if (fis != null) {
                try {
                    while (fis.read(buf).also { length = it } > 0) {
                        fos.write(buf, 0, length)
                    }

                    fis.close()
                } catch (ex :Exception) {
                    Log.d("customOptionFileWriteFail", "${ex.message}")
                }
            }
        Log.d("custom fis value", "$fis")
        fos.close()
       return backUpdir

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