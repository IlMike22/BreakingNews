package com.androiddevs.mvvmnewsapp.data.repository

import com.androiddevs.mvvmnewsapp.data.remote.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.data.remote.database.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article

class NewsRepository(
    val database: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(
            countryCode = countryCode,
            pageNumber = pageNumber
        )

    suspend fun searchNews(query: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(query, pageNumber)

    fun getSavedNews() = database.getDao().getAllArticles()

    suspend fun insertOrUpdate(article: Article) {
        database.getDao().insertOrUpdateArticel(article)
    }

    suspend fun deleteArticle(article: Article) = database.getDao().deleteArticle(article)
}