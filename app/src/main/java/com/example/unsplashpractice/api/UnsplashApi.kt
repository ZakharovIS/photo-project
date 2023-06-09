package com.example.unsplashpractice.api

import com.example.unsplashpractice.auth.TokenStorage
import com.example.unsplashpractice.data.models.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhotosList(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Query("page") page: Int = 1,
        @Query("per_page") per_page: Int = 100,
        @Query("order_by") order_by: String = "popular"
    ): Response<List<PreviewPhoto>>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Query("query") query: String,
        @Query("lang") lang: String = "ru",
        @Query("page") page: Int = 1,
        @Query("per_page") per_page: Int = 30,
    ): Response<SearchResult>

    @POST("photos/{id}/like")
    suspend fun likePhoto(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Path("id") id: String
    ): Response<UnsplashLikeResponse>

    @DELETE("photos/{id}/like")
    suspend fun unlikePhoto(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Path("id") id: String
    ): Response<UnsplashLikeResponse>

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Path("id") id: String
    ): Response<UnsplashPhoto>


    @GET("collections")
    suspend fun getCollectionsList(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Query("page") page: Int = 1
    ): Response<List<UnsplashCollection>>

    @GET("collections/{id}")
    suspend fun getCollection(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Path("id") id: String
    ): Response<UnsplashCollection>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Path("id") id: String,
        @Query("page") page: Int = 1
    ): Response<List<PreviewPhoto>>

    @GET("me")
    suspend fun getMyAccountDetails(
        @Header("Authorization") authHeader: String = getApiKey(),
    ): Response<UserPrivate>


    @GET("users/{username}/likes")
    suspend fun getMyLikesPhotos(
        @Header("Authorization") authHeader: String = getApiKey(),
        @Path("username") username: String,
        @Query("page") page: Int = 1
    ): Response<List<PreviewPhoto>>

    companion object {

        private const val BASE_URL = "https://api.unsplash.com/"
        //private const val API_KEY = "6f423dc0c5904b908f5161220231401"

        fun create(): UnsplashApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(UnsplashApi::class.java)
        }

        fun getApiKey(): String {
            return "Bearer " + TokenStorage.accessToken
        }


    }

}

