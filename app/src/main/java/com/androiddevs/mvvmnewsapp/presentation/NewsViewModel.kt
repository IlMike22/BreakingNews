package com.androiddevs.mvvmnewsapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.data.remote.api.dto.NewsResponse
import com.androiddevs.mvvmnewsapp.data.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.data.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

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

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                return Resource.Success(newsResponse)
            }
        }

        return Resource.Error(message = response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                return Resource.Success(newsResponse)
            }
        }

        return Resource.Error(message = response.message())
    }
}