package com.example.newsapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.data.Article
import com.example.newsapp.data.NewsService
import com.example.newsapp.databinding.FragmentNewsBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NewsFragment : Fragment(), ArticleAdapter.OnArticleClickListener, ArticleAdapter.OnArticleLongClickListener {
    private lateinit var binding: FragmentNewsBinding
    private lateinit var articleAdapter: ArticleAdapter
    private var favoriteArticlesList: MutableList<Article> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val infoBtn = binding.infoBtn
        infoBtn.setOnClickListener { showPopup() }

        val categorySpinner = binding.categorySpinner
        val recyclerView = binding.recyclerView

        articleAdapter = ArticleAdapter(emptyList(), this,this)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = articleAdapter

        val categories = arrayOf("Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                loadArticles(selectedCategory)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        loadArticles("Business")
    }



    private fun loadArticles(category: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val articles = getArticlesByCategory(category)
            withContext(Dispatchers.Main) {
                articleAdapter.submitList(articles)
            }
        }
    }

    private fun showPopup() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("How the app works")
            .setMessage("Long press to save article to favorites \n\nTap to open article in browser")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
        alertDialog.show()
    }


    private fun getArticlesByCategory(category: String): List<Article> {
        val apiKey = "dbd673defd6942febc67b3f5540fe01d"


        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NewsService::class.java)

        val call = service.getArticlesByCategory("se", apiKey, category)
        val response = call.execute()

        return response.body()?.articles ?: emptyList()

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

    override fun onArticleLongClick(article: Article) {
        addArticleToFavorites(article)
    }
}