package com.example.unsplashpractice.data

import com.example.unsplashpractice.api.UnsplashApi
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.data.models.UnsplashCollection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashCollectionsApiRepository @Inject constructor(
    private val unsplashApi: UnsplashApi
) {

    suspend fun getCollectionsListPaged(page: Int): List<UnsplashCollection> {
        return unsplashApi.getCollectionsList(page = page).body()!!
    }

    suspend fun getCollection(id: String): UnsplashCollection {
        val collectionResult = try {
            unsplashApi.getCollection(id = id)
        } catch (exception: Exception) {
            null
        }

        return if (collectionResult != null && collectionResult.isSuccessful) {
            collectionResult.body()!!
        } else {
            UnsplashCollection()
        }

    }

    suspend fun getCollectionPhotos(page: Int, id: String): List<PreviewPhoto> {
        return unsplashApi.getCollectionPhotos(id = id, page = page).body()!!
    }

}