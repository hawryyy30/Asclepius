package com.dicoding.asclepius.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ClassificationVerdict::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun useDao(): DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null

        fun getDatabase(context: Context): AppDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context, AppDB::class.java, "app_db"
                ).build()
            }
        }

    }
}