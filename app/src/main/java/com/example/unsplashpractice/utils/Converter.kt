package com.example.unsplashpractice.utils


import com.example.unsplashpractice.data.models.*
import com.example.unsplashpractice.db.Photo

object Converter {
    fun convertUnsplashPhotoToDbPhoto(previewPhoto: PreviewPhoto): Photo {
        return Photo(
            previewPhoto.id!!,
            previewPhoto.likes,
            previewPhoto.liked_by_user?: false,
            previewPhoto.user?.name,
            previewPhoto.user?.username,
            previewPhoto.user?.profile_image?.medium,
            previewPhoto.urls?.regular
        )
    }

    fun convertDbPhotoToUnsplashPhoto(dBPhoto: Photo?): UnsplashPhoto? {
        if (dBPhoto != null) {
            return UnsplashPhoto(
                id = dBPhoto.pid,
                likes = dBPhoto.likes,
                liked_by_user = dBPhoto.likeByMe,
                user = User(
                    name = dBPhoto.name,
                    username = dBPhoto.userName,
                    profile_image = ProfileImage(
                        medium = dBPhoto.userPhotoUrl
                    )
                ),
                urls = Urls(regular = dBPhoto.photoUrl)
            )
        } else return null
    }
}