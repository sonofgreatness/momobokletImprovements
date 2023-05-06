package com.example.momobooklet_by_sm.data.di

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.example.momobooklet_by_sm.MainActivity
import com.example.momobooklet_by_sm.R
import com.example.momobooklet_by_sm.common.util.classes.WRITETO
import com.example.momobooklet_by_sm.data.local.Database
import com.example.momobooklet_by_sm.data.local.daos.CommissionDao
import com.example.momobooklet_by_sm.data.local.daos.TransactionDao
import com.example.momobooklet_by_sm.data.local.daos.UserAccountsDao
import com.example.momobooklet_by_sm.data.local.repositories.CommisionDatesManagerImpl
import com.example.momobooklet_by_sm.data.local.repositories.CommissionRepositoryImpl
import com.example.momobooklet_by_sm.data.local.repositories.TransactionRepositoryImpl
import com.example.momobooklet_by_sm.data.local.repositories.UserRepositoryImpl
import com.example.momobooklet_by_sm.domain.repositories.*
import com.example.momobooklet_by_sm.domain.services.csv.CsvConfigImpl
import com.example.momobooklet_by_sm.domain.services.pdf.PdfConfigImpl
import com.wwdablu.soumya.simplypdf.composers.properties.TableProperties
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.FileOutputStream
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)// to ensure components are live throughout app's lifetime
object AppModule {



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

/*
    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun providesCsvConfigRepository(app: Application,
                                    prefix: String,
                                    datesManager: CommissionDatesManagerRepository,
                                    location: WRITETO,
                                    suffix: String = datesManager.generateTodayDate(),
                                     fileName: String = "$prefix-$suffix.csv",
                                    @Suppress("DEPRECATION")
                                    hostPath: String = Environment.getExternalStorageDirectory().path
                                         .plus("/").plus(app.getString(R.string.app_name))
                                         .plus("/CSV")
                                    ,
                                    internalhostPath:String = app.filesDir.path,

                                    otherHostPath: FileOutputStream? = app.openFileOutput(fileName, Context.MODE_APPEND)

    ) :CsvConfigRepository{

        return  CsvConfigImpl(app,prefix,
                              datesManager,location,
                              suffix,fileName, hostPath,
                              internalhostPath,otherHostPath
                               )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun providesPdfConfigRepository(
         app: Application,
         prefix: String,
         datesManager: CommissionDatesManagerRepository,
         location: WRITETO,
        suffix: String = datesManager.generateTodayDate(),

        tableProperties: TableProperties = TableProperties().apply{
            borderColor = "#000000"
            borderWidth = 1
            drawBorder = true
        },
         fileName: String = "$prefix-$suffix.pdf",
        @Suppress("DEPRECATION")
        hostPath: String = Environment.getExternalStorageDirectory().path
            .plus("/").plus(app.getString(R.string.app_name))
            .plus("/PDF")
        ,
        internalhostPath:String = app.filesDir.path,
         otherHostPath: FileOutputStream? = app.openFileOutput(fileName, Context.MODE_APPEND)
        ) :PdfConfigRepository{


        return PdfConfigImpl(app,prefix, datesManager,
                                location,suffix,tableProperties,
                                fileName, hostPath,
                                internalhostPath, otherHostPath
                              )
    }
*/
}