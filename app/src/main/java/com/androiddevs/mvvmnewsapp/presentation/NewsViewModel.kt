package com.androiddevs.mvvmnewsapp.presentation

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.data.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.data.util.Resource
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    private val repository: NewsRepository,
    app: Application
) : AndroidViewModel(app) {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(query: String) = viewModelScope.launch {
        safeSearchCall(query)
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

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getBreakingNews(
                    countryCode = countryCode,
                    pageNumber = breakingNewsPage
                )

                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error(message = "No internet connection"))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> breakingNews.postValue(Resource.Error(message = "Network failure"))
                else -> breakingNews.postValue(Resource.Error(message = "Conversion error"))
            }
        }
    }

    private suspend fun safeSearchCall(query: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.searchNews(
                    query = query,
                    pageNumber = searchNewsPage
                )

                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error(message = "No internet connection"))
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> searchNews.postValue(Resource.Error(message = "Network failure"))
                else -> searchNews.postValue(Resource.Error(message = "Conversion error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) { // need SDK 23
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager
                .getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }

        return false
    }
}
