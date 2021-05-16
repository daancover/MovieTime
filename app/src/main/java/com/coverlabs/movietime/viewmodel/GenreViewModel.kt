package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.viewModelScope
import com.coverlabs.di.error.ErrorHandler
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.MovieRepository
import com.coverlabs.movietime.viewmodel.base.BaseViewModel
import com.coverlabs.movietime.viewmodel.base.StateMutableLiveData
import kotlinx.coroutines.launch

class GenreViewModel(private val movieRepository: MovieRepository) : BaseViewModel() {

    private val movieList = StateMutableLiveData<List<Movie>>()

    private val searchError = ErrorHandler { error ->
        movieList.postError(error)
    }

    fun onMovieListResult() = movieList.toLiveData()

    fun searchMovie(genre: String) {
        viewModelScope.launch(searchError.handler) {
            movieList.postLoading()
            val movies = movieRepository.searchMovies(genre = genre)
            movieList.postSuccess(movies)
        }
    }
}