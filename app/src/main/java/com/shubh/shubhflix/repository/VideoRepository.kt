package com.shubh.shubhflix.repository

import com.shubh.shubhflix.data.apiresponse.Hit
import com.shubh.shubhflix.data.apiresponse.Medium
import com.shubh.shubhflix.helper.API
import com.shubh.shubhflix.interfaces.PexelsApi
import javax.inject.Inject

class VideoRepository @Inject constructor(private val api: PexelsApi) {

    suspend fun getVideos(query: String): List<Hit> {
        val response = api.getPopularVideos(API.API_KEY, query, true)
        if (response.isSuccessful) {
            return response.body()?.hits ?: emptyList()
        } else {
            throw Exception("Failed to fetch videos: ${response.message()}")
        }
    }
}