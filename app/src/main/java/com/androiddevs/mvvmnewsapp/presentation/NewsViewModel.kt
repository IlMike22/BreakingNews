package com.androiddevs.mvvmnewsapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.data.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.data.util.Resource
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            breakingNews.postValue(Resource.Loading())
            val result = repository.getBreakingNews(
                countryCode = countryCode,
                pageNumber = breakingNewsPage
            )

            breakingNews.postValue(handleBreakingNewsResponse(result))
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            searchNews.postValue(Resource.Loading())
            val result = repository.searchNews(query, searchNewsPage)
            searchNews.postValue(handleSearchNewsResponse(result))
        }
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            repository.deleteArticle(article)
        }
    }

    fun getSavedNews() = repository.getSavedNews()

    fun insertOrUpdateArticle(article: Article) {
        viewModelScope.launch {
            repository.insertOrUpdate(article)
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                breakingNewsPage++

                if (breakingNewsResponse == null) {
                    breakingNewsResponse = newsResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = newsResponse.articles
                    oldArticles?.addAll(newArticles)
                }

                return Resource.Success(breakingNewsResponse ?: newsResponse)
            }
        }

        return Resource.Error(message = response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { searchResponse ->
                searchNewsPage++

                if (searchNewsResponse == null) {
                    searchNewsResponse = searchResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = searchResponse.articles
                    oldArticles?.addAll(newArticles)
                }

                return Resource.Success(searchNewsResponse ?: searchResponse)
            }
        }

        return Resource.Error(message = response.message())
    }
}