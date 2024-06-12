package com.dicoding.asclepius.view.result

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.apis.ArticleData
import com.dicoding.asclepius.databinding.ArticleCardBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ArticlesAdapter : ListAdapter<ArticleData, ArticlesAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding: ArticleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleData) {
            Glide.with(binding.imageViewArticle.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(binding.imageViewArticle)
            binding.tvArticleTitle.text = article.title ?: "Title Not Available"
            binding.tvSource.text = article.author ?: "N/A"
            binding.tvReleaseDate.text = article.publishedAt?.let { dateString ->
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEE, dd MM yyyy, HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "N/A"
            } ?: "N/A"
            val maxLength = 100
            binding.tvArticleContent.text = article.content?.let {
                if (it.length > maxLength) {
                    sanitizeHtml(it.substring(0, maxLength) + "...")
                } else {
                   sanitizeHtml(it)
                }
            } ?: "Content not available"
            binding.btnReadMore.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                binding.root.context.startActivity(intent)
            }
        }

        fun sanitizeHtml(input: String): String {
            val htmlTagPattern = "<[^>]+>".toRegex()
            return input.replace(htmlTagPattern, "")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ArticleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleData>() {
            override fun areItemsTheSame(prevItem: ArticleData, nextItem: ArticleData): Boolean {
                return prevItem == nextItem
            }

            override fun areContentsTheSame(prevItem: ArticleData, nextItem: ArticleData): Boolean {
                return prevItem == nextItem
            }
        }
    }

}


