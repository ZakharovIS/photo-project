package com.example.unsplashpractice.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PreviewPhoto(
    @Json(name = "id") var id: String? = null,
    @Json(name = "width") var width: Int? = null,
    @Json(name = "height") var height: Int? = null,
    @Json(name = "color") var color: String? = null,
    @Json(name = "likes")  var likes: Int? = null,
    @Json(name = "user") var user: User? = null,
    @Json(name = "urls") var urls: Urls? = null,
    @Json(name = "liked_by_user")  var liked_by_user: Boolean? = null
)




