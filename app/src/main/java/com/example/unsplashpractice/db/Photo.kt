package com.example.unsplashpractice.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey
    @ColumnInfo(name = "pid")
    val pid: String,
    @ColumnInfo(name = "likes")
    val likes: Int?,
    @ColumnInfo(name = "likeByMe")
    val likeByMe: Boolean,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "userName")
    val userName: String?,
    @ColumnInfo(name = "userPhotoUrl")
    val userPhotoUrl: String?,
    @ColumnInfo(name = "photoUrl")
    val photoUrl: String?
)

