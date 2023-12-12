package my.free.data.apis.backend.repositoryTests

import com.free.momobooklet_by_sm.data.dto.AuthTokenDto
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.common.util.classes.Role
import com.free.momobooklet_by_sm.data.dto.transaction.TransactionRequest
import com.free.momobooklet_by_sm.data.dto.user.AuthenticationRequest
import com.free.momobooklet_by_sm.data.dto.user.UserRegistrationRequest
import com.free.momobooklet_by_sm.data.remote.repositories.BackEndApi
import com.free.momobooklet_by_sm.domain.repositories.transaction.TransactionBackEndRepository
import com.free.momobooklet_by_sm.domain.repositories.user.BackEndUserRepository
import com.google.common.truth.Truth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.net.HttpURLConnection
import java.sql.Timestamp
import java.util.*

class TransactionBackEndRepositoryTest {


    private  lateinit var time  : Timestamp
    private lateinit var  testApi : BackEndApi
    private  lateinit var  userRepository : BackEndUserRepository
    private  lateinit var  underTest : TransactionBackEndRepository
    private  lateinit var  accessToken : String


    private lateinit var  testUser: UserRegistrationRequest
    private lateinit var  testUserAuthDetails : AuthenticationRequest
    private lateinit var  incorrectTestUserAuthDetails : AuthenticationRequest

    private  lateinit var testTransaction: TransactionRequest
    private  lateinit var incorrectTestTransaction: TransactionRequest



    class OAuthInterceptor(private val tokenType: String, private val accessToken: String) :
        Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            request = request.newBuilder().header("Authorization", "$tokenType $accessToken").build()

            return chain.proceed(request)
        }
    }


    class BackEndTransactionRepositoryImplTest(val testApi: BackEndApi) : TransactionBackEndRepository{
        override suspend fun addTransaction(request: TransactionRequest?, token :String): Response<ResponseBody> {
            return   testApi.addTransaction(request, token)
        }

        override suspend fun getAllTransactions(token: String): Response<ResponseBody>? {
            return  testApi.getAllTransactions(token)
        }

    }




    /*
    *Change the methods in this class according to implementation
    *  you wish to test
    * ---------------------------------------------------------
    * in this case there is a server application running on
    *     machine and the machine's IP address is
    *     store in BASE_URL3 variable
    *----------------------------------------------------------
    **/
    class BackEndUserRepositoryImplTest(val testApi: BackEndApi) : BackEndUserRepository {
        override suspend fun addUser(request: UserRegistrationRequest?): Response<ResponseBody?> {
            return testApi.register(request)
        }

        override suspend fun authenticateUser(request: AuthenticationRequest?): Response<ResponseBody> {
            return  testApi.authenticate(request)
        }
    }





    @Before
    fun setUp() {

        //INITIALIZE   RETROFIT HTTP CLIENT
        val logging= HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()


        testApi = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL3)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create()



        userRepository = BackEndUserRepositoryImplTest(testApi)

        underTest = BackEndTransactionRepositoryImplTest(testApi)

        testUser = UserRegistrationRequest(
            "+26876911464",
            "momo pay",
            "password",
            "email.js",
            Role.USER)

        testUserAuthDetails = AuthenticationRequest(
            "+26876911464",
            "password")

        incorrectTestUserAuthDetails =
            AuthenticationRequest(
                "+2687691464",
                "password")


        time   = Timestamp(System.currentTimeMillis())

        testTransaction =
            TransactionRequest(
                UUID.randomUUID().toString(),
                null,
                "customer_name",
                "customer_id",
                "customer_phone",
                false,
                109.8F,
                "+26876911464",
                arrayListOf(0,0,0)
            )


        incorrectTestTransaction = TransactionRequest(
            "uuid1",
            time,
            "customer_name",
            "customer_id",
            "customer_phone",
            false,
            109.8F,
            "does not exist",
            arrayListOf(0,0,0)
        )


        runBlocking {
            accessToken = setAccessTokenFrom(userRepository.authenticateUser(testUserAuthDetails))
        }


    }



    @After
    fun tearDown() {
    }





    /*==============================================================================================
    *                    TRANSACTION TESTS
    *=============================================================================================*/

    @Test
    fun canAddTransactionToDB(){

        runBlocking{







            //when
            val actualResponse = underTest.addTransaction(testTransaction, "Bearer $accessToken")
            val actualResponseFalse = underTest.addTransaction(testTransaction,"Bearer does'nt exit")

            //then
           Truth.assertThat(actualResponse.code()).isEqualTo(HttpURLConnection.HTTP_OK)

            Truth.assertThat(actualResponseFalse.code()).isNotEqualTo(HttpURLConnection.HTTP_OK)
        }
    }



    /*==============================================================================================
    *             END     OF     TRANSACTION TESTS
    *=============================================================================================*/


    private fun setAccessTokenFrom(actualResponse :Response<ResponseBody>) : String{

        val gson  = Gson()
        val listType = object : TypeToken<AuthTokenDto>(){}.type
        val   authToken : AuthTokenDto = gson.fromJson(actualResponse.body()?.string(),listType)

        return  authToken.accessToken
    }



}