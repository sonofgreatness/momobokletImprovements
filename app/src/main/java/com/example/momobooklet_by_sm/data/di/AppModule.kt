package com.example.momobooklet_by_sm.data.di

import android.app.Activity
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.common.util.Constants
import com.example.momobooklet_by_sm.data.local.Database
import com.example.momobooklet_by_sm.data.local.daos.CommissionDao
import com.example.momobooklet_by_sm.data.local.daos.TransactionDao
import com.example.momobooklet_by_sm.data.local.daos.UserAccountsDao
import com.example.momobooklet_by_sm.data.local.repositories.*
import com.example.momobooklet_by_sm.data.remote.SheetsDbApi
import com.example.momobooklet_by_sm.data.remote.repositories.RemoteTransactionsRepositoryImpl
import com.example.momobooklet_by_sm.domain.repositories.*
import com.example.momobooklet_by_sm.domain.repositories.ConnectivityObserver
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