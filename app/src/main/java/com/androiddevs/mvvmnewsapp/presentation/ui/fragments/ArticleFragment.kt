package com.androiddevs.mvvmnewsapp.presentation.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.presentation.NewsViewModel
import com.androiddevs.mvvmnewsapp.presentation.ui.NewsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()
    val TAG = "ArticleFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        fab.setOnClickListener {
            try {
                viewModel.insertOrUpdateArticle(article)
                Snackbar.make(view, "Article saved successfull.", Snackbar.LENGTH_SHORT).show()
            } catch (exception: Exception) {
                Log.e(TAG, "Cannot delete article")
            }
        }
    }
}