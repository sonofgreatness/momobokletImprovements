package com.free.momobooklet_by_sm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.free.momobooklet_by_sm.data.local.daos.CommissionDao
import com.free.momobooklet_by_sm.data.local.daos.TransactionDao
import com.free.momobooklet_by_sm.data.local.daos.UserAccountsDao
import com.free.momobooklet_by_sm.data.local.models.*


@Database
    (
    entities = [TransactionModel::class, UserModel::class, CommissionModel::class,
        DailyCommissionModel::class, PeriodicCommissionModel::class,
        DailyCommissionModel_FTS::class, TransactionModel_FTS::class,BACKUP_METADATA::class
    ], version = 1, exportSchema = false
)

    abstract class Database : RoomDatabase() {
    abstract fun getDao(): TransactionDao
    abstract fun getUserAccountsDao(): UserAccountsDao
    abstract fun getCommissionDao(): CommissionDao

    //initialize database
    companion object {
        @Volatile// other threads can immediately see when a thread changes this instance
        private var INSTANCE: com.free.momobooklet_by_sm.data.local.Database? = null
        fun getDatabase(context: Context): com.free.momobooklet_by_sm.data.local.Database {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.free.momobooklet_by_sm.data.local.Database::class.java,
                    "user_database_test"
                )
                   .createFromAsset("databases/timestamped.db")
                    .addCallback(CALLBACK2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
    /********************************************************
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



