package com.example.unsplashpractice.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") var id: String? = null,
    @Json(name = "username") var username: String? = null,
    @Json(name = "name") var name: String? = null,
    @Json(name = "bio") var bio: String? = null,
    @Json(name = "location") var location: String? = null,
    @Json(name = "total_likes") var total_likes: Int? = null,
    @Json(name = "profile_image") var profile_image: ProfileImage? = null,
    @Json(name = "total_photos") var total_photos: Int? = null,
    @Json(name = "total_collections") var total_collections: Int? = null,

)

@JsonClass(generateAdapter = true)
data class ProfileImage(
    @Json(name = "small") var small: String? = null,
    @Json(name = "medium") var medium: String? = null,
    @Json(name = "large") var large: String? = null
)