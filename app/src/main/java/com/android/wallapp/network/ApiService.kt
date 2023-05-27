package com.android.wallapp.network

import com.android.wallapp.models.CategoryResponse
import com.android.wallapp.models.PhotoResponse

import com.android.wallapp.network.NetworkingConstants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {


    @Headers("Accept-Version: v1", "Authorization: Client-ID $API_KEY")
    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<PhotoResponse>


    @Headers("Accept-Version: v1", "Authorization: Client-ID $API_KEY")
    @GET("topics/{id}/photos")
    suspend fun getTopicPhotos(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<PhotoResponse>

    @Headers("Accept-Version: v1", "Authorization: Client-ID $API_KEY")
    @GET("topics")
    suspend fun getTopics(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<CategoryResponse>
}