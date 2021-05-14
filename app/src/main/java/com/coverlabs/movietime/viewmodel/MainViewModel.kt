package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coverlabs.domain.repository.MovieRepository
import kotlinx.coroutines.launch

class MainViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    fun getTopFiveMovies() {
        viewModelScope.launch {
            val movies = movieRepository.getTopFiveMovies()
            println("Movie name: ${movies[0].title}")
        }
    }
}