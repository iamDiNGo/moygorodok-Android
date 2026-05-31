package com.gorod.moygorodok.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.News
import com.gorod.moygorodok.databinding.ItemNewsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(
    private val onNewsClick: (News) -> Unit
) : ListAdapter<News, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.textTitle.text = news.title
            binding.textSummary.text = news.summary.orEmpty()
            binding.textSummary.visibility = if (news.summary.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.textDate.text = formatDate(news.publishedAt)
            binding.textCategory.text = sourceLabel(news.sourceType)
            binding.textCategory.visibility = if (news.sourceType.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.textViews.visibility = View.GONE

            binding.root.setOnClickListener {
                onNewsClick(news)
            }
        }

        private fun sourceLabel(source: String?): String = when (source) {
            "manual" -> "От редакции"
            "rss" -> "RSS"
            else -> source.orEmpty()
        }

        private fun formatDate(iso: String?): String {
            if (iso.isNullOrBlank()) return ""
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ROOT)
                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru", "RU"))
                parser.parse(iso)?.let(formatter::format) ?: iso
            } catch (e: Exception) {
                iso
            }
        }
    }

    private class NewsDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }
}
