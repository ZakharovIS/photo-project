package com.example.unsplashpractice.data.models

import android.util.Log
import com.example.unsplashpractice.db.Photo
import com.example.unsplashpractice.db.PhotoDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotosDatabaseRepository @Inject constructor(
    private val photoDao: PhotoDao
) {

suspend fun getPagedList(limit: Int, offset: Int): List<Photo> {
    Log.d("DB_PH", "${photoDao.getPagedList(limit, offset)}")

    return photoDao.getPagedList(limit, offset)
}

   suspend fun addPhotoToDB(photo: Photo) {

       photoDao.insert(photo)
    }

    suspend fun deleteAllDb() {
        photoDao.getAll().forEach {
            photo -> photoDao.delete(photo)
        }
    }

    suspend fun getPhoto(id: String): Photo? {
        return photoDao.checkPhotoID(id)
    }

}