package com.gorod.moygorodok.ui.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.Movie
import com.gorod.moygorodok.data.model.Showtime
import com.gorod.moygorodok.data.model.MockCinemas

class MovieDetailViewModel : ViewModel() {

    private val _movie = MutableLiveData<Movie>()
    val movie: LiveData<Movie> = _movie

    private val _showtimes = MutableLiveData<List<Showtime>>()
    val showtimes: LiveData<List<Showtime>> = _showtimes

    fun loadMovie(movieId: String) {
        MockCinemas.getMovieById(movieId)?.let { movie ->
            _movie.value = movie
            _showtimes.value = MockCinemas.getShowtimesForMovie(movieId)
        }
    }
}
