package com.shubh.shubhflix.data.apiresponse

data class Hit(
    val comments: Int,
    val downloads: Int,
    val duration: Int,
    val id: Int,
    val likes: Int,
    val pageURL: String,
    val tags: String,
    val type: String,
    val user: String,
    val userImageURL: String,
    val user_id: Int,
    val videos: Videos,
    val views: Int
)