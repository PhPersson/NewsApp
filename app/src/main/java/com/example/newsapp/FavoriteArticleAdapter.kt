package com.example.newsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.data.Article

class FavoriteArticleAdapter(private var articles: List<Article>, private val clickListener: OnArticleClickListener, private val longClickListener: OnArticleLongClickListener): RecyclerView.Adapter<FavoriteArticleAdapter.FavoriteArticleViewHolder>() {

    inner class FavoriteArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleTitle: TextView = itemView.findViewById(R.id.titleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)
        return FavoriteArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.articleTitle.text = article.title

        holder.itemView.setOnClickListener{
            clickListener.onArticleClick(article)

        }

        holder.itemView.setOnLongClickListener {
            longClickListener.onArticleLongClick(article)
            true
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun updateList(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }


    interface OnArticleClickListener {
        fun onArticleClick(article: Article)
    }
    interface OnArticleLongClickListener {
        fun onArticleLongClick(article:Article)
    }

}
