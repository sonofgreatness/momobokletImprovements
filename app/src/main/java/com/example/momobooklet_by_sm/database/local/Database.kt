package com.example.momobooklet_by_sm.database.local

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.Database
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.momobooklet_by_sm.database.local.daos.CommissionDao
import com.example.momobooklet_by_sm.database.local.daos.DatabaseDao
import com.example.momobooklet_by_sm.database.local.daos.UserAccountsDao
import com.example.momobooklet_by_sm.database.local.models.*

@Database
    (
    entities = [
        TransactionModel::class, UserModel::class, CommissionModel::class,
        DailyCommissionModel::class, PeriodicCommissionModel::class,
        DailyCommissionModel_FTS::class, TransactionModel_FTS::class
    ], version = 1, exportSchema = false
)
    abstract class Database : RoomDatabase() {
    abstract fun getDao(): DatabaseDao
    abstract fun getUserAccountsDao(): UserAccountsDao
    abstract fun getCommissionDao(): CommissionDao

    //initialize database
    companion object {
        @Volatile// other threads can immediately see when a thread changes this instance
        private var INSTANCE: com.example.momobooklet_by_sm.database.local.Database? = null
        fun getDatabase(context: Context): com.example.momobooklet_by_sm.database.local.Database {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.momobooklet_by_sm.database.local.Database::class.java,
                    "user_database"
                )
                    .createFromAsset("databases/user_database.db")
                    .addCallback(CALLBACK2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }

    /*********************************************************
     * TRIGGERS FTS REBUILDS
     *********************************************************/
    private object CALLBACK2 : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL("INSERT INTO RECORD_SHEET_FTS(RECORD_SHEET_FTS) VALUES ('rebuild')")
            db.execSQL("INSERT INTO DailyCommission_FTS(DailyCommission_FTS) VALUES ('rebuild')")
        }
    }

}



