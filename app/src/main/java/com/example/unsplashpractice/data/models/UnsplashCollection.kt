package com.example.unsplashpractice.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnsplashCollection(
    @Json(name = "id") var id: String? = null,
    @Json(name = "title") var title: String? = null,
    @Json(name = "description") var description: String? = null,
    @Json(name = "total_photos") var total_photos: Int? = null,
    @Json(name = "cover_photo") var cover_photo: PreviewPhoto? = null,
    @Json(name = "user") var user: User? = null,
    @Json(name = "tags") var tags: List<Tag?> = emptyList()
)


