package com.example.unsplashpractice.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationUnsplash(
    @Json(name = "city") var city: String? = null,
    @Json(name = "country") var country: String? = null,
    @Json(name = "position") var position: Position? = null
)

@JsonClass(generateAdapter = true)
class Position (
    @Json(name = "latitude") var latitude: Double? = null,
    @Json(name = "longitude") var longitude: Double? = null
)
