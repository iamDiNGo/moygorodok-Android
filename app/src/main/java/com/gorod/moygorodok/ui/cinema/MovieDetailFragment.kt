package com.gorod.moygorodok.ui.cinema

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gorod.moygorodok.databinding.FragmentMovieDetailBinding

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModels()
    private val args: MovieDetailFragmentArgs by navArgs()
    private lateinit var adapter: ShowtimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapter()
        observeViewModel()

        viewModel.loadMovie(args.movieId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapter() {
        adapter = ShowtimeAdapter { showtime ->
            Toast.makeText(
                context,
                "Билет на ${showtime.time} в ${showtime.cinema.name} (${showtime.hall}) - ${showtime.price} ₽",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.recyclerShowtimes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MovieDetailFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.movie.observe(viewLifecycleOwner) { movie ->
            binding.apply {
                toolbar.title = movie.title
                textTitle.text = movie.title
                textOriginalTitle.text = movie.originalTitle
                textGenre.text = movie.genre
                textDuration.text = "${movie.duration} мин"
                textRating.text = movie.rating.toString()
                textAgeRating.text = movie.ageRating
                textDescription.text = movie.description
                textDirector.text = movie.director
                textCast.text = movie.cast.joinToString(", ")
                textReleaseDate.text = movie.releaseDate

                // Set poster color
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
            }
        }

        viewModel.showtimes.observe(viewLifecycleOwner) { showtimes ->
            adapter.submitList(showtimes)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
