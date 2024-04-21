package com.example.newsapp.data

data class NewsSearchResponse(
    val totalResults: Int,
    val articles: List<Article>
)
