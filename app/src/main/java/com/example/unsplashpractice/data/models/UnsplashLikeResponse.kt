package com.example.unsplashpractice.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnsplashLikeResponse(
    @Json(name = "photo") var photo: PreviewPhoto? = null,
    @Json(name = "user") var user: User? = null
)






