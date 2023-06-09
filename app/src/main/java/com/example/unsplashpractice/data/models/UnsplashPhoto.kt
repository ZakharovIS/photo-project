package com.example.unsplashpractice.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnsplashPhoto(
    @Json(name = "id") var id: String? = null,
    @Json(name = "width") var width: Int? = null,
    @Json(name = "height") var height: Int? = null,
    @Json(name = "color") var color: String? = null,
    @Json(name = "downloads") var downloads: Int? = null,
    @Json(name = "likes") var likes: Int? = null,
    @Json(name = "liked_by_user") var liked_by_user: Boolean = false,
    @Json(name = "user") var user: User? = null,
    @Json(name = "urls") var urls: Urls? = null,
    @Json(name = "exif") var exif: Exif? = null,
    @Json(name = "location") var location: LocationUnsplash? = null,
    @Json(name = "tags") var tags: List<Tag>? = null,
)

@JsonClass(generateAdapter = true)
data class Exif(
    @Json(name = "make") var make: String? = null,
    @Json(name = "model") var model: String? = null,
    @Json(name = "name") var name: String? = null,
    @Json(name = "exposure_time") var exposure_time: String? = null,
    @Json(name = "aperture") var aperture: String? = null,
    @Json(name = "focal_length") var focal_length: String? = null,
    @Json(name = "iso") var iso: Int? = null
)




