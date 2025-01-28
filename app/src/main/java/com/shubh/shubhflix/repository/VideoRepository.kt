package com.shubh.shubhflix.repository

import com.shubh.shubhflix.data.apiresponse.Hit
import com.shubh.shubhflix.data.apiresponse.Medium
import com.shubh.shubhflix.interfaces.PexelsApi
import javax.inject.Inject

class VideoRepository @Inject constructor(private val api: PexelsApi) {

    suspend fun getVideos( query: String): List<Hit> {
        val response = api.getPopularVideos("48486794-5056413700d16e0de5163b6e8", query, true)
        if (response.isSuccessful) {
            return response.body()?.hits ?: emptyList()
        } else {
            throw Exception("Failed to fetch videos: ${response.message()}")
        }
    }
}