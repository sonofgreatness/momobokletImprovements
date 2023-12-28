package com.free.momobooklet_by_sm.domain.use_cases.manage_users

import android.app.Application
import android.content.Context
import android.content.OperationApplicationException
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.Constants.Companion.AUTH_PREFS_NAME
import com.free.momobooklet_by_sm.common.util.classes.operationalStates.Resource
import com.free.momobooklet_by_sm.data.dto.AuthTokenDto
import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.domain.repositories.user.BackEndUserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class AuthenticateUserInBackEndUseCase
@Inject
constructor(val repository: BackEndUserRepository,
            val application:Application) {

    operator fun invoke(request : AuthenticationRequest) : Flow<Resource<String>> = flow{

        try{
            emit(Resource.Loading("Registration Loading"))
            val response = repository.authenticateUser(request)
            if (response.isSuccessful) {

                saveUserTokenToSharePrefs(response,request)
                emit(Resource.Success(Constants.BACKEND_AUTH_OK))

            }else
                emit(Resource.Error(Constants.BACKEND_AUTH_FAIL))
        }
        catch(ex: Exception){
            emit(Resource.Error(Constants.BACKEND_AUTH_FAIL))
            Timber.d("${Constants.BACKEND_REG_FAIL} ${ex.message}")
        }


    }


    /*****************************************************************************************
     * extracts username and accessToken from request ,
     *       writes  to sharedprefs
     *                 username(phone number) = key , acccessToken = value
     *@param response :  httpResponse expected form JSON:
     *                    { accessToken :accessToken-Value,
     *                       refreshToken :refreshToken-Value
     *                    }
     ************************************************************************************/
    private fun saveUserTokenToSharePrefs(
        response: Response<ResponseBody>,
        request: AuthenticationRequest
    ) {
         val username  = request.username
        val accessToken = getAccessToken(response)
        writeToSharedPrefs(username,accessToken)
        Timber.d("SHARED PREFS TESTS $username ====> $accessToken")
    }

    private fun writeToSharedPrefs(username: String, accessToken: String) {

        val sharedPreference =  application.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString(username,accessToken)
        editor.apply()
    }


    private fun getAccessToken(response: Response<ResponseBody>): String {
        val gson  = Gson()
        val listType = object : TypeToken<AuthTokenDto>(){}.type
        val   authToken : AuthTokenDto = gson.fromJson(response.body()?.string(),listType)
        return  authToken.accessToken
    }

}
