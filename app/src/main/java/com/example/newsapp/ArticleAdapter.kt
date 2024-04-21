package com.example.newsapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.data.Article


class ArticleAdapter(private var articles: List<Article>, private val clickListener: OnArticleClickListener, private val longClickListener: OnArticleLongClickListener) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]

        holder.titleTextView.text = article.title

        holder.itemView.setOnClickListener {
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

    fun submitList(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }


    interface OnArticleClickListener {
        fun onArticleClick(article: Article)
    }
    interface OnArticleLongClickListener {
        fun onArticleLongClick(article: Article)
    }
}
