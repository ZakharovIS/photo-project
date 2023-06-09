package com.example.unsplashpractice.db

import androidx.room.*

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos")
    suspend fun getAll(): List<Photo>

    @Query("SELECT * FROM photos LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(limit: Int, offset: Int): List<Photo>

    @Query("SELECT * FROM photos WHERE pid = :id")
    suspend fun checkPhotoID(id: String): Photo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)

}