package com.androiddevs.mvvmnewsapp.data.remote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.androiddevs.mvvmnewsapp.data.remote.api.dto.Source

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id :Int? = null,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
)
