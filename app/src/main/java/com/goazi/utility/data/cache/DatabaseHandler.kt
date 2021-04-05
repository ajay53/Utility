package com.goazi.utility.data.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.goazi.utility.data.cache.dao.CallDao
import com.goazi.utility.model.Call

@Database(entities = [Call::class], version = 1, exportSchema = false)
abstract class DatabaseHandler : RoomDatabase() {

    abstract fun callDao(): CallDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseHandler? = null

        fun getInstance(context: Context): DatabaseHandler? {
            if (INSTANCE != null) {
                return INSTANCE
            }
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseHandler::class.java,
                    "db_utility"
                ).fallbackToDestructiveMigration()
                    .build()
                return INSTANCE
            }
        }
    }
}