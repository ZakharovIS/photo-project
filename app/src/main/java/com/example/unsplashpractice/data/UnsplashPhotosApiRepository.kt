package com.example.unsplashpractice.data

import com.example.unsplashpractice.api.UnsplashApi
import com.example.unsplashpractice.data.models.PreviewPhoto
import com.example.unsplashpractice.data.models.UnsplashPhoto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotosApiRepository @Inject constructor(
    private val unsplashApi: UnsplashApi
) {

    suspend fun getPhotosListPaged(page: Int): List<PreviewPhoto> {
          return unsplashApi.getPhotosList(page = page).body()!!
    }

    suspend fun searchPhotosListPaged(query: String, page: Int): List<PreviewPhoto> {

           return unsplashApi.searchPhotos(query = query ,page = page).body()!!.results

    }

    suspend fun getPhoto(id: String): UnsplashPhoto? {
        return unsplashApi.getPhoto(id = id).body()
    }

    suspend fun setLikeToPhoto(id: String): PreviewPhoto {
        val likeSetResult = try {
            unsplashApi.likePhoto(id = id)
        } catch (exception: Exception) {
            null
        }

        return if (likeSetResult != null && likeSetResult.isSuccessful) {
            likeSetResult.body()?.photo!!
        } else {
            PreviewPhoto()
        }
    }

    suspend fun removeLikeFromPhoto(id: String): PreviewPhoto {
        val likeRemoveResult = try {
            unsplashApi.unlikePhoto(id = id)
        } catch (exception: Exception) {
            null
        }

        return if (likeRemoveResult != null && likeRemoveResult.isSuccessful) {
            likeRemoveResult.body()?.photo!!
        } else {
            PreviewPhoto()
        }
    }


}