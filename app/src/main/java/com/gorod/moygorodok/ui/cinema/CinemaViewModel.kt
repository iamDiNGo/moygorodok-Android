package com.gorod.moygorodok.ui.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.Movie
import com.gorod.moygorodok.data.model.MockCinemas

class CinemaViewModel : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadMovies()
    }

    private fun loadMovies() {
        _isLoading.value = true
        _movies.value = MockCinemas.getMovies()
        _isLoading.value = false
    }

    fun refresh() {
        loadMovies()
    }
}
