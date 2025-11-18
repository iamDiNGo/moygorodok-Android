package com.gorod.moygorodok.ui.cinema

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gorod.moygorodok.data.model.Movie
import com.gorod.moygorodok.databinding.ItemMovieBinding

class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                textTitle.text = movie.title
                textGenre.text = movie.genre
                textDuration.text = "${movie.duration} мин"
                textRating.text = movie.rating.toString()
                textAgeRating.text = movie.ageRating

                // Set poster placeholder color
                try {
                    posterPlaceholder.setBackgroundColor(Color.parseColor(movie.posterColor))
                } catch (e: Exception) {
                    posterPlaceholder.setBackgroundColor(Color.parseColor("#37474F"))
                }

                // Movie icon based on genre
                val icon = when {
                    movie.genre.contains("Фантастика") -> "🚀"
                    movie.genre.contains("Драма") -> "🎭"
                    movie.genre.contains("Боевик") -> "💥"
                    movie.genre.contains("Триллер") -> "😱"
                    movie.genre.contains("Комедия") -> "😂"
                    movie.genre.contains("Ужасы") -> "👻"
                    else -> "🎬"
                }
                textPosterIcon.text = icon

                root.setOnClickListener {
                    onMovieClick(movie)
                }
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}
