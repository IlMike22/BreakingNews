package com.androiddevs.mvvmnewsapp.data.remote.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.data.remote.entity.Article

@Dao
interface IArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateArticel(articel: Article)

    @Query("SELECT * FROM articles")
    fun getArticels(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}