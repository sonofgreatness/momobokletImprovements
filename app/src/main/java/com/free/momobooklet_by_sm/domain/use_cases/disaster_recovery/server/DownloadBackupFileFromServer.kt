package com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery.server

import android.app.Application
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.workDataOf
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.BackUpState
import com.free.momobooklet_by_sm.domain.repositories.backup.BackupBackEndRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DownloadBackupFileFromServer @Inject constructor(
    val repository: BackupBackEndRepository,
    val application: Application
)
{

    operator fun invoke(username: String,backupId : String) : Flow<BackUpState> = flow{

        try{
            emit(BackUpState.Loading)
            val accessToken = getAccessTokenFromSharedPrefs(username)

            if (accessToken != null) {
                val params = mapOf("backupId" to backupId)
                val response = repository.download(params,"Bearer $accessToken")


                if (response.isSuccessful == true) {
                    val zipFile = getBackUpFromServer(response.body())
                    emit(BackUpState.Success(zipFile))
                } else
                    emit(
                        BackUpState.Error("error code = ${response.code().toString()}"
                                +"\ntoken = $accessToken\n username = $username"))
            }
            else
            {
                emit(BackUpState.Error("Access Token null  -> "+ Constants.BACKUP_FROM_SERVER_FAIL))
            }

        }
        catch(ex: Exception){
            emit(BackUpState.Error("Exception"+ Constants.BACKUP_FROM_SERVER_FAIL))
            Timber.d("Exception ${Constants.BACKUP_FROM_SERVER_FAIL} ${ex.message}")
        }
    }

    /**
     * gets backup file stored in server
     *@param body  ->  file returned by server response
     *@return Return file in application cache  with contents
     *                   of   body  parameter
    **/
    @Throws(IOException::class)
    private fun getBackUpFromServer(body: ResponseBody?): File {
        val zipFile = File(application.cacheDir, Constants.BACKUP_FROM_SERVER_FILENAME)
        if (zipFile.exists())
            zipFile.delete()
        zipFile.createNewFile()

        body?.let { anybody ->

                val outputStream = FileOutputStream(zipFile)
                outputStream.use { stream ->
                    try {
                        stream.write(anybody.bytes())
                    } catch(e: IOException) {
                        throw e
                    }

                }
        }
            return zipFile
    }


    private fun getAccessTokenFromSharedPrefs(username: String): String? {
        val sharedPreference =  application.getSharedPreferences(Constants.AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreference.getString(username,"defaultName")
    }
}