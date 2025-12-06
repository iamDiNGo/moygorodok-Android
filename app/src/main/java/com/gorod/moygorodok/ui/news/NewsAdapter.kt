package com.gorod.moygorodok.ui.news

import android.view.LayoutInflater
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

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        fun bind(news: News) {
            binding.textTitle.text = news.title
            binding.textSummary.text = news.summary
            binding.textCategory.text = news.category.displayName
            binding.textDate.text = dateFormat.format(news.publishedAt)
            binding.textViews.text = "${news.viewCount}"

            // Если есть изображение, можно загрузить через Glide/Coil
            // binding.imageNews.load(news.imageUrl)

            binding.root.setOnClickListener {
                onNewsClick(news)
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
