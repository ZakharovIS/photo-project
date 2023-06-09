package com.example.unsplashpractice.di

import android.content.Context
import com.example.unsplashpractice.db.AppDatabase
import com.example.unsplashpractice.db.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule() {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getAppDatabaseInstance(context = context)
    }

    @Singleton
    @Provides
    fun getWeatherDao(appDatabase: AppDatabase): PhotoDao {
        return appDatabase.getPhotoDao()
    }

}

