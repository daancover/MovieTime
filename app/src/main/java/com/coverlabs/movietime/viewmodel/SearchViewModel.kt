package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.MovieRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val movieList = MutableLiveData<List<Movie>>()

    fun onMovieListResult(): LiveData<List<Movie>> = movieList

    fun searchMovie(title: String) {
        viewModelScope.launch {
            val movies = movieRepository.searchMovies(title = title)
            movieList.postValue(movies)
        }
    }
}