package com.example.newsapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.newsapp.databinding.FragmentFavoritesBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.data.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoritesFragment : Fragment(), FavoriteArticleAdapter.OnArticleClickListener, FavoriteArticleAdapter.OnArticleLongClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteArticleAdapter: FavoriteArticleAdapter
    private lateinit var binding: FragmentFavoritesBinding
    private var favoriteArticlesList: MutableList<Article> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.favoriteArticlesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        favoriteArticleAdapter = getFavoriteArticles()?.let { FavoriteArticleAdapter(it, this, this) }!!
        recyclerView.adapter = favoriteArticleAdapter
    }

    override fun onResume() {
        super.onResume()
        favoriteArticleAdapter = getFavoriteArticles()?.let { FavoriteArticleAdapter(it, this, this) }!!
    }

    private fun getFavoriteArticles(): List<Article>? {

        val context = requireContext()
        val sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)

        val articlesJson = sharedPreferences.getString("favorite_articles", "")

        val type = object : TypeToken<List<Article>>() {}.type
        val favoriteArticlesList: List<Article>? = Gson().fromJson(articlesJson, type)

        if (favoriteArticlesList.isNullOrEmpty()) {
            "No saved favorites".also { binding.favoritesStatusTextView.text = it }
        } else {
            "Your favorites".also { binding.favoritesStatusTextView.text = it }
        }
        return favoriteArticlesList
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
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Article")
            .setMessage("Do you want to delete this article from your favorites?")
            .setPositiveButton("Yes") { _, _ ->
                deleteArticleFromFavorites(article)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteArticleFromFavorites(article: Article) {
        val context = requireContext()
        val sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        val articlesJson = sharedPreferences.getString("favorite_articles", "")
        val type = object : TypeToken<List<Article>>() {}.type
        favoriteArticlesList = Gson().fromJson<MutableList<Article>?>(articlesJson, type).toMutableList()


        favoriteArticlesList.removeAll { it.title == article.title }


        val updatedArticlesJson = Gson().toJson(favoriteArticlesList)
        editor.putString("favorite_articles", updatedArticlesJson)
        editor.apply()
        lifecycleScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Default) {
                favoriteArticleAdapter.updateList(favoriteArticlesList)
            }
        }

        Toast.makeText(context, "Article removed from favorites", Toast.LENGTH_SHORT).show()
    }

}

