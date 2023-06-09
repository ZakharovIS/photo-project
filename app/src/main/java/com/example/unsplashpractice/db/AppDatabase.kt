package com.example.unsplashpractice.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Photo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {


    abstract fun getPhotoDao(): PhotoDao

    companion object {
        private var db_instance: AppDatabase? = null

        fun getAppDatabaseInstance(context: Context): AppDatabase {
            if (db_instance == null) {
                db_instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "photo_db"
                ).allowMainThreadQueries()
                 .build()
            }
            return db_instance!!
        }
    }
}