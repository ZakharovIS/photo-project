package com.example.unsplashpractice.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Urls(
    @Json(name = "raw") var raw: String? = null,
    @Json(name = "full") var full: String? = null,
    @Json(name = "regular") var regular: String? = null,
    @Json(name = "small") var small: String? = null,
    @Json(name = "thumb") var thumb: String? = null
)
