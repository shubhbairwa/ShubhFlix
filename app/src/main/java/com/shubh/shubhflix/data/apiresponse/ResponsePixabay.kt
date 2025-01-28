package com.shubh.shubhflix.data.apiresponse

data class ResponsePixabay(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
)