package com.example.unsplashpractice.data

import com.example.unsplashpractice.api.UnsplashApi
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.data.models.UserPrivate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserApiRepository @Inject constructor(
    private val unsplashApi: UnsplashApi
) {

    suspend fun getPhotoLikedByMePaged(username: String, page: Int): List<PreviewPhoto> {

        return unsplashApi.getMyLikesPhotos(username = username, page = page).body()!!

    }

    suspend fun getMyData(): UserPrivate? {
        return unsplashApi.getMyAccountDetails().body()

    }


}