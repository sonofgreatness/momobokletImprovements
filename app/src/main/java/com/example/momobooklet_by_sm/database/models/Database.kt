package com.example.momobooklet_by_sm.database.models

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.Database
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.momobooklet_by_sm.database.daos.DatabaseDao
import com.example.momobooklet_by_sm.database.daos.UserAccountsDao

@Database
    (entities=[TransactionModel::class,UserModel::class],version=1, exportSchema = false)
abstract class Database : RoomDatabase(){
    abstract  fun getDao():DatabaseDao
    abstract  fun getUserAccountsDao() :UserAccountsDao


    //initialize database
    companion object {
        @Volatile// other threads can immediately see when a thread changes this instance
        private var INSTANCE: com.example.momobooklet_by_sm.database.models.Database?=null
        fun getDatabase(context: Context): com.example.momobooklet_by_sm.database.models.Database {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.momobooklet_by_sm.database.models.Database::class.java,
                    "user_database")

                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }


        private object CALLBACK : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val ABC_Mobile = "ABC Mobile"
                val phone = "+26876787878"
                val email = "test@email.com"
                val password = "No"
                val control = false

                val queryString: String =
                    "INSERT INTO `user_accounts` (MoMoName, MoMoNumber,AgentEmail, AgentPassword,IsIncontrol) " +
                            "VALUES" +"("+ABC_Mobile+","+phone+","+email+","+password+","+0+")"
                db.execSQL(queryString)
                Log.d("dummyAgentInsert", "Insert Made")
            }
        }


    }

}



