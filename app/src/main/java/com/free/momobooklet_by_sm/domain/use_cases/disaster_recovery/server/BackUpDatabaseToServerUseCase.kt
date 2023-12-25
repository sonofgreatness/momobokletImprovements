package com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery.server

import android.app.Application
import android.content.Context
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.BackUpState
import com.free.momobooklet_by_sm.domain.repositories.backup.BackupBackEndRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class BackUpDatabaseToServerUseCase @Inject constructor(
    val repository: BackupBackEndRepository,
    val application: Application
)
{

    operator fun invoke(username: String) : Flow<BackUpState> = flow{

        try{
            emit(BackUpState.Loading)
            val accessToken = getAccessTokenFromSharedPrefs(username)

            if (accessToken != null) {

                val zipFile = File(application.cacheDir, Constants.BACKUP_FILENAME)
                val requestFile: RequestBody = zipFile
                    .asRequestBody("application/zip".toMediaTypeOrNull())
                val body : MultipartBody.Part
                        = MultipartBody.Part.createFormData("file", zipFile.name, requestFile)
                val response = repository.upload(body,"Bearer $accessToken")

                if (response?.isSuccessful == true) {

                    Timber.d("Upload Backups  ==>"+response.body()?.string())
                    emit(BackUpState.Success(zipFile))
                } else
                    emit(
                        BackUpState.Error("error code = "+response?.code().toString()
                                +"\ntoken = $accessToken\n username = ${username}"))
            }
            else
            {
                emit(BackUpState.Error("Access Token null  -> "+ Constants.BACKEND_BACKUP_ADD_FAIL))
            }

        }
        catch(ex: Exception){
            emit(BackUpState.Error("Exception"+ Constants.BACKEND_BACKUP_ADD_FAIL))
            Timber.d("Exception"+ Constants.BACKEND_REG_FAIL +ex.message)
        }
    }


    private fun getAccessTokenFromSharedPrefs(username: String): String? {
        val sharedPreference =  application.getSharedPreferences(Constants.AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreference.getString(username,"defaultName")
    }

}