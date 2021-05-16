package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.coverlabs.di.error.ErrorHandler
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.repository.MovieRepository
import com.coverlabs.domain.repository.StorageRepository
import com.coverlabs.movietime.viewmodel.base.StateMutableLiveData
import kotlinx.coroutines.launch

class SearchViewModel(
    private val movieRepository: MovieRepository,
    storageRepository: StorageRepository
) : MovieListViewModel(storageRepository) {

    private val movieList = StateMutableLiveData<List<Movie>>(true)

    private val searchError = ErrorHandler { error ->
        movieList.postError(error)
    }

    fun onMovieListResult() = movieList.toLiveData()

    @OnLifecycleEvent(ON_CREATE)
    fun getAllMovies() {
        searchMovie()
    }

    fun searchMovie(title: String = "") {
        viewModelScope.launch(searchError.handler) {
            movieList.postLoading()
            val movies = movieRepository.searchMovies(title = title)
            movieList.postSuccess(movies)
        }
    }
}