package my.free.data.apis.backend.repositoryTests

import com.free.momobooklet_by_sm.data.dto.AuthTokenDto
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.Role
import com.free.momobooklet_by_sm.data.dto.BackUpDetailsDto
import com.free.momobooklet_by_sm.data.dto.BackUpDetailsDtoItem
import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
import com.free.momobooklet_by_sm.data.remote.repositories.BackEndApi
import com.free.momobooklet_by_sm.domain.repositories.backup.BackupBackEndRepository
import com.free.momobooklet_by_sm.domain.repositories.user.BackEndUserRepository
import com.google.common.truth.Truth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.File
import java.net.HttpURLConnection


class BackUpBackEndRepositoryTest {

    /**
     * DATABASE IS PREPOPULATED WITH  RECORDS
     *
     *   USER RECORDS
     *     RECORD1 =  User("string","Abx","stud","email",Role.USER)
     *     RECORD2 =   User("bliss","Abx","stud","email",Role.USER)
     *===============================================================================================
     * BACKUP RECORDS
     *       RECORD1  = Backup (2023-11-12 04:32:50.95,3d90429f-70a3-417c-a14d-be9c546df2da,164340)
     *       RECORD 2 = Backup (2023-11-12 04:33:30.872,865d8c76-f0ca-4440-b456-7054172a6759,164341)
     **/




    private lateinit var  testApi : BackEndApi
    private  lateinit var  userRepository : BackEndUserRepository
    private  lateinit var  underTest : BackupBackEndRepository
    private  lateinit var  accessToken : String
    private lateinit var  testUser: UserRegistrationRequest
    private lateinit var  testUserAuthDetails : AuthenticationRequest





    class BackEndBackUpRepositoryImplTest(val testApi: BackEndApi) : BackupBackEndRepository{
        override suspend fun download(
            params: Map<String, String>,
            token: String
        ): Response<ResponseBody> {
            return  testApi.download(params,token)
        }

        override suspend fun upload(file: MultipartBody.Part, token: String): Response<ResponseBody> {
            return testApi.upload(file, token)
        }

        override suspend fun getListOfbackups(token: String): Response<ResponseBody> {
            return  testApi.getListOfbackups(token)
        }


    }

    @Before
    fun setUp(){


        //INITIALIZE   RETROFIT HTTP CLIENT
        val logging= HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            //.addInterceptor(logging)
            .build()


        testApi = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL3)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create()



        userRepository = TransactionBackEndRepositoryTest.BackEndUserRepositoryImplTest(testApi)
        underTest = BackEndBackUpRepositoryImplTest(testApi)



            testUser = UserRegistrationRequest(
                "string",
                "Abx",
                "stud",
                "email",
                Role.USER)

            testUserAuthDetails = AuthenticationRequest(
                "string",
                "stud")

        runBlocking {
            accessToken = setAccessTokenFrom(userRepository.authenticateUser(testUserAuthDetails))
        }


    }

    @After
    fun tearDown() {
    }



    @Test
    fun canUploadBackupFileTest(){

       runBlocking {
           println("access token ")
           println(accessToken)
          val file = File("testBackUploadFile.zip")
           file.createNewFile()
            // create RequestBody from file
           val requestFile: RequestBody = file
               .asRequestBody("application/zip".toMediaTypeOrNull())
            val body : MultipartBody.Part
                   = MultipartBody.Part.createFormData("file", file.name, requestFile)

           //when
           val actualResponse = underTest.upload(body,"Bearer $accessToken")
           //then
           Truth.assertThat(actualResponse.code()).isEqualTo(HttpURLConnection.HTTP_OK)
           Truth.assertThat(file.exists()).isEqualTo(true)

           file.delete()
           println("access token ")
           println(accessToken)
       }
    }

    @Test
    fun canDownloadBackupFileTest(){

       runBlocking {


           val params = mapOf("backupId" to "865d8c76-f0ca-4440-b456-7054172a6759")
           val paramsFailed = mapOf("backupId" to "does not exist")
           //when
           val actualResponse = underTest.download(params, "Bearer $accessToken")
           val actualResponseFailed = underTest.download(paramsFailed, "Bearer $accessToken")

           //then
           Truth.assertThat(actualResponse.code()).isEqualTo(HttpURLConnection.HTTP_OK)
           Truth.assertThat(actualResponseFailed.code()).isEqualTo(HttpURLConnection.HTTP_UNAUTHORIZED)

           println("access token ")
           println(accessToken)
       }
    }



    @Test
    fun `can get list of backup files for user`(){
       runBlocking {
           val actualResponse = underTest.getListOfbackups("Bearer $accessToken")
           println("OUTPUT :")
           val responseAsString = actualResponse.body()?.string()
           println(responseAsString)
           val list = getBackupDetaillsFromRequest(responseAsString!!)
           println("OUTPUT22 :")
           println(list.toString())
       }

    }
    private fun setAccessTokenFrom(actualResponse :Response<ResponseBody>) : String{
        val gson  = Gson()
        val listType = object : TypeToken<AuthTokenDto>(){}.type
        val   authToken : AuthTokenDto = gson.fromJson(actualResponse.body()?.string(),listType)

        return  authToken.accessToken
    }

    private fun getBackupDetaillsFromRequest(actualResponse: String): List<BackUpDetailsDtoItem> {
        val gson = Gson()
        val listType = object : TypeToken<BackUpDetailsDto>() {}.type
        return gson.fromJson(actualResponse, listType)
    }
}