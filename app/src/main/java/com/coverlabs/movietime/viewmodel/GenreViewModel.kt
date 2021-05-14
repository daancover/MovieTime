package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.MovieRepository
import kotlinx.coroutines.launch

class GenreViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val movieList = MutableLiveData<List<Movie>>()

    fun onMovieListResult(): LiveData<List<Movie>> = movieList

    fun searchMovie(genre: String) {
        viewModelScope.launch {
            val movies = movieRepository.searchMovies(genre = genre)
            movieList.postValue(movies)
        }
    }
}