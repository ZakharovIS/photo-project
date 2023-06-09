package com.example.unsplashpractice.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tag(
    @Json(name = "type") var type: String? = null,
    @Json(name = "title") var title: String? = null
)
