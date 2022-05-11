package com.example.momobooklet_by_sm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.momobooklet_by_sm.database.model.TransactionModel
import com.example.momobooklet_by_sm.database.model.UserModel



@Database
    (entities = [UserModel::class,TransactionModel::class],version=1 , exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null
        val tempInstance = INSTANCE


        fun getDatabase(context: Context): UserDatabase{


            if (tempInstance != null) {

                return tempInstance
            }

            synchronized(lock=this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "Main_database"

                ).build()
                INSTANCE=instance
                return instance
            }



        }


    }




}