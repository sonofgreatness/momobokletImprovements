package com.free.momobooklet_by_sm.data.remote.repositories.backup

import com.free.momobooklet_by_sm.data.remote.repositories.BackEndApi
import com.free.momobooklet_by_sm.domain.repositories.backup.BackupBackEndRepository
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class BackEndBackupRepoImpl @Inject constructor(val api: BackEndApi) :BackupBackEndRepository{

    override suspend fun download(
        params: Map<String, String>,
        token: String
    ): Response<ResponseBody>  = api.download(params,token)

    override suspend fun upload(file: MultipartBody.Part, token: String)
    = api.upload(file,token)

    override suspend fun getListOfbackups(token: String): Response<ResponseBody>
    = api.getListOfbackups(token)
}