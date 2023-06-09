package com.example.unsplashpractice.di

import android.content.Context
import com.example.unsplashpractice.sharedprefs.TokensStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SharedPrefsModule {
    @Singleton
    @Provides
    fun provideSharedPreferencesObject(@ApplicationContext context: Context) : TokensStorage{
        return TokensStorage(context)
    }
}