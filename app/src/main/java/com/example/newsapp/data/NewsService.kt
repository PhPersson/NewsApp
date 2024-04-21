package com.example.newsapp.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {


    @GET("top-headlines")
    fun getArticlesByCategory(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String,
        @Query("category") category: String
    ): Call<NewsResponse>


    @GET("top-headlines")
    fun searchForArticles(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String,
        @Query("q") query: String
    ): Call<NewsSearchResponse>


}