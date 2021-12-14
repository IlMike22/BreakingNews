package com.androiddevs.mvvmnewsapp.data.remote.api.dto

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)