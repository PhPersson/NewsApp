package com.example.newsapp

import android.widget.Toast
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.data.Article
import com.example.newsapp.data.NewsService
import com.example.newsapp.databinding.FragmentSearchBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment(), ArticleAdapter.OnArticleClickListener, ArticleAdapter.OnArticleLongClickListener{

    private lateinit var binding: FragmentSearchBinding
    private lateinit var articleAdapter: ArticleAdapter
    private var favoriteArticlesList: MutableList<Article> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val searchBtn = binding.searchBtn
        searchBtn.setOnClickListener { searchArticles() }

        val recyclerView = binding.recyclerView

        articleAdapter = ArticleAdapter(emptyList(), this,this)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = articleAdapter

    }

    private fun searchArticles(){
        if (binding.searchField.text.isNotBlank()) {

            lifecycleScope.launch(Dispatchers.IO) {
                val articles = getArticles(binding.searchField.text.toString())
                withContext(Dispatchers.Main) {
                    articleAdapter.submitList(articles)
                }
            }
        } else {
            binding.searchField.error = "The searchfield cant be empty"
            return
        }
    }

    private fun getArticles(query: String): List<Article> {
        val apiKey = "dbd673defd6942febc67b3f5540fe01d"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NewsService::class.java)

        val call = service.searchForArticles("se", apiKey, query)
        val response = call.execute()
        return if (response.body()?.articles?.isEmpty() == true){
            Handler(Looper.getMainLooper()).post {
                binding.searchField.error = "Could not find any articles on that topic!"
            }
            emptyList()
        } else
            response.body()?.articles ?: emptyList()
    }


    override fun onArticleClick(article: Article) {
        AlertDialog.Builder(requireContext())
            .setTitle("Open Article")
            .setMessage("Do you want to open this article in a browser?")
            .setPositiveButton("Open") { _, _ ->
                val articleUrl = article.url
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
                startActivity(browserIntent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addArticleToFavorites(article: Article) {
        val context = requireContext()
        val sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)

        if (sharedPreferences.contains(article.title)) {
            Toast.makeText(context, "This article is already in your favorites.", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(context)
                .setTitle("Add to Favorites")
                .setMessage("Do you want to add this article to your favorites?")
                .setPositiveButton("Yes") { _, _ ->
                    saveArticlesToSharedPreferences(article)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun saveArticlesToSharedPreferences(article: Article) {

        favoriteArticlesList.add(article)
        val context = requireContext()
        val sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val articlesJson = Gson().toJson(favoriteArticlesList)

        editor.putString("favorite_articles", articlesJson)
        editor.apply()
        editor.commit()
        Toast.makeText(context, "Article saved to favorites", Toast.LENGTH_SHORT).show()
    }

    override fun onArticleLongClick(article: Article) {
        addArticleToFavorites(article)
    }
}
