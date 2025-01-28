package com.shubh.shubhflix.interfaces

import com.shubh.shubhflix.data.apiresponse.ResponsePixabay
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Query

interface PexelsApi {
    @GET("videos")
    suspend fun getPopularVideos(  @Query("key") apiKey: String,
                                   @Query("q") query: String,
                                   @Query("pretty") pretty: Boolean): Response<ResponsePixabay>
}