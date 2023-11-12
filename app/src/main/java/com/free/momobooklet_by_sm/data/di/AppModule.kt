package com.free.momobooklet_by_sm.data.di

import com.free.momobooklet_by_sm.data.remote.repositories.backup.BackEndBackupRepoImpl
import com.free.momobooklet_by_sm.domain.repositories.backup.BackupBackEndRepository

import android.app.Activity
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.free.momobooklet_by_sm.MainActivity
import com.free.momobooklet_by_sm.common.util.Constants
import com.free.momobooklet_by_sm.data.local.Database
import com.free.momobooklet_by_sm.data.local.daos.CommissionDao
import com.free.momobooklet_by_sm.data.local.daos.TransactionDao
import com.free.momobooklet_by_sm.data.local.daos.UserAccountsDao
import com.free.momobooklet_by_sm.data.local.repositories.*
import com.free.momobooklet_by_sm.data.remote.SheetsDbApi
import com.free.momobooklet_by_sm.data.remote.repositories.BackEndApi
import com.free.momobooklet_by_sm.data.remote.repositories.user.BackEndUserRepositoryImpl
import com.free.momobooklet_by_sm.data.remote.repositories.RemoteTransactionsRepositoryImpl
import com.free.momobooklet_by_sm.data.remote.repositories.transaction.TransactionBackEndRepoImpl
import com.free.momobooklet_by_sm.domain.repositories.*
import com.free.momobooklet_by_sm.domain.repositories.ConnectivityObserver
import com.free.momobooklet_by_sm.domain.repositories.transaction.TransactionBackEndRepository
import com.free.momobooklet_by_sm.domain.repositories.user.BackEndUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // to ensure components are live throughout app's lifetime
object AppModule {

    @Provides
    @Singleton
    fun providesRemoteApi(): SheetsDbApi{
        val logging= HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun providesRemoteTransactionsRepository (sheetsDbApi: SheetsDbApi): RemoteTransactionsRepository
    {
        return RemoteTransactionsRepositoryImpl(sheetsDbApi)
    }


    @Provides
    @Singleton
    fun providesBackEndApi(): BackEndApi {
        val logging= HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
         .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL3)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun providesBackEndUserRepository (api: BackEndApi): BackEndUserRepository
    {
        return BackEndUserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun providesBackEndTransactionRepository (api: BackEndApi): TransactionBackEndRepository
    {
        return TransactionBackEndRepoImpl(api)
    }

    @Provides
    @Singleton
    fun providesBackEndBackUpRepository(api: BackEndApi): BackupBackEndRepository
    {
        return BackEndBackupRepoImpl(api)
    }

    /*...
     * BEGINNING OF LOCAL
     * ...*/

    @Provides
    @Singleton //to ensure single instance
    fun providesActivity(): Activity{
        return MainActivity()
    }

    @Provides
    @Singleton //to ensure single instance
    fun providesCommissionDao(app : Application): CommissionDao{
        return Database.getDatabase(app).getCommissionDao()
    }

    @Provides
    @Singleton //to ensure single instance
    fun providesTransactionDao(app : Application): TransactionDao{
        return Database.getDatabase(app).getDao()
    }
    @Provides
    @Singleton //to ensure single instance
    fun providesUserAccountsDao(app : Application): UserAccountsDao{
        return Database.getDatabase(app).getUserAccountsDao()
    }

    @Provides
    @Singleton
    fun providesCommissionRepository(commissionDao: CommissionDao) : CommissionRepository {
        return  CommissionRepositoryImpl(commissionDao)
    }

    @Provides
    @Singleton
    fun providesTransactionRepository(transactionDao: TransactionDao) : TransactionRepository {
        return  TransactionRepositoryImpl(transactionDao)
    }
    @Provides
    @Singleton
    fun providesUserRepository(userDao: UserAccountsDao) : UserRepository{
        return  UserRepositoryImpl(userDao)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun providesCommissionDatesManagerRepository(app:Application) : CommissionDatesManagerRepository{
        return  CommisionDatesManagerImpl(app)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun providesReportRepository(app:Application) : ReportConfigRepository{
        return  ReportConfigRepositoryImpl(app)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun providesConnectivityObserver(app:Application) : ConnectivityObserver {
        return  NetworkConnectivityObserver(app.applicationContext)
    }

}