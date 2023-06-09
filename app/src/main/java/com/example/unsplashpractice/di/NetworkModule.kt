package com.example.unsplashpractice.di

import com.example.unsplashpractice.api.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule() {
    @Singleton
    @Provides
    fun provideUnsplashApi(): UnsplashApi {
        return UnsplashApi.create()
    }

}


