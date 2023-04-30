package com.example.momobooklet_by_sm.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
        DailyCommissionModel_FTS::class, TransactionModel_FTS::class,BACKUP_METADATA::class
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
                    "user_database.db"
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
    /********************************************************
     * TRIGGERS FTS REBUILDS
     *********************************************************/
    private object CALLBACK2 : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            /*-----------------------------------------------
               MAKE SURE Commission_Chart is populated
            ------------------------------------------------*/
           /* db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(0,20.0,125.0,1,1.0)")
            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(1,20.0,125.0,0,1.5)")

            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(2,126.0,250.0,1,2.0)")
            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(3,126.0,250.0,0,3.0)")

            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(4,251.0,500.0,1,3.0)")
            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(5,251.0,500.0,0,6.0)")

            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(6,501.0,1000.0,1,6.0)")
            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(7,501.0,1000.0,0,9.0)")

            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(8,1001.0,2000.0,1,9.0)")
            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(9,1001.0,2000.0,0,12.0)")

            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(10,2001.0,100000.0,1,12.0)")
            db.execSQL("INSERT INTO Commission_Chart(Row_Id,Min,Max,Type,Commission_Amount)VALUES(11,2001.0,100000.0,0,18.0)")
*/
            db.execSQL("INSERT INTO RECORD_SHEET_FTS(RECORD_SHEET_FTS) VALUES ('rebuild')")
            db.execSQL("INSERT INTO DailyCommission_FTS(DailyCommission_FTS) VALUES ('rebuild')")
        }
    }

}



