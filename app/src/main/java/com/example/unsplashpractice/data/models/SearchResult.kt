package com.example.unsplashpractice.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    @Json(name = "total") var total: Int? = null,
    @Json(name = "total_pages") var total_pages: Int? = null,
    @Json(name = "results") var results: List<PreviewPhoto>

)


