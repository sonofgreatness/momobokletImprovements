package com.free.momobooklet_by_sm.domain.use_cases.disaster_recovery.server

import android.app.Application
import android.content.Context
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.backup.BackUpDetailsDto
import com.free.momobooklet_by_sm.domain.repositories.backup.BackupBackEndRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.*
import javax.inject.Inject

class GetBackupDetailsFromServer @Inject constructor(
    val repository: BackupBackEndRepository,
    val application: Application)
{

    operator fun invoke(username: String) : Flow<Resource<String>> = flow{

        try{
            emit(Resource.Loading())
            val accessToken = getAccessTokenFromSharedPrefs(username)

            if (accessToken != null) {
               val  response =  repository.getListOfbackups("Bearer $accessToken")
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    writeToCache(json)
                    emit(Resource.Success("Succcess got"+json!!))
                } else
                    emit(
                        Resource.Error("error code = "+ response.code().toString()
                                +"\ntoken = $accessToken\n username = ${username}"))
            }
            else
            {
                emit(Resource.Error("Access Token null  -> "+ Constants.BACKEND_BACKUP_ADD_FAIL))
            }

        }
        catch(ex: Exception){
            emit(Resource.Error("Exception"+ Constants.BACKEND_BACKUP_ADD_FAIL))
            Timber.d("Exception ${Constants.BACKEND_REG_FAIL} +${ex.message}")
        }
    }
    @Throws(IOException::class)
    private fun writeToCache(detailsJson  :String?) {
       Timber.d ("write to cache called => $detailsJson")
        val temFile = File(application.cacheDir, Constants.BACKUP_DETAILS_FILE)

        if(temFile.exists())
             temFile.delete()
        temFile.createNewFile()

        try {
            val fileWriter = FileWriter(temFile)
            fileWriter.write(detailsJson)
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception as needed
           throw e
        }

    }



    private fun getAccessTokenFromSharedPrefs(username: String): String? {
        val sharedPreference =  application.getSharedPreferences(Constants.AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreference.getString(username,"defaultName")
    }

    /**
     * reads file in cache ,  converts string read
     *    to  List<BackupDetails>
     */
    fun getBackupDetailsFromFile(): List<BackUpDetails>{

        val returnList :MutableList<BackUpDetails> =ArrayList()
        val textFromFile = readBackupfile()
        val gson = Gson()
        val listType = object : TypeToken<BackUpDetailsDto>(){}.type
        val backUpDetails : BackUpDetailsDto = gson.fromJson(textFromFile,listType)

        backUpDetails.forEach{
             val temp =  BackUpDetails(
                time = it.getTimestampInHumanReadableForm(),
                 backupdId = it.getBackupId()
             )

            returnList.add(temp)

        }
        return returnList
    }

    /********************************************************
     * reads text in cache file temp_backup_details.json
     *@return Returns jsonArray as string
    ******************************************************/
    private fun readBackupfile(): String?{

        val file = File(application.cacheDir, Constants.BACKUP_DETAILS_FILE)
        if (!file.exists()) {
            // File does not exist
            return null
        }

        val stringBuilder = StringBuilder()

        try {
            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }

            fileInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception as needed
        }

        return stringBuilder.toString()
    }


}