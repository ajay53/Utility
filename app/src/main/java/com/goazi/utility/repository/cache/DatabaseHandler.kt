package com.goazi.utility.repository.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.goazi.utility.repository.cache.dao.CallDao
import com.goazi.utility.repository.cache.dao.UnlockDao
import com.goazi.utility.model.Call
import com.goazi.utility.model.Unlock

@Database(entities = [Call::class, Unlock::class], version = 2, exportSchema = false)
abstract class DatabaseHandler : RoomDatabase() {

    abstract fun callDao(): CallDao
    abstract fun unlockDao(): UnlockDao

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