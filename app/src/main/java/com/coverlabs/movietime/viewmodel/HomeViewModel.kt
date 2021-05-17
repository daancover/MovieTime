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

class HomeViewModel(
    private val movieRepository: MovieRepository,
    storageRepository: StorageRepository
) : MovieListViewModel(storageRepository) {

    private val movieList = StateMutableLiveData<List<Movie>>(true)

    private val topFiveError = ErrorHandler { error ->
        movieList.postError(error)
    }

    fun onMovieListResult() = movieList.toLiveData()

    @OnLifecycleEvent(ON_CREATE)
    fun getTopFiveMovies() {
        movieList.postLoading()
        viewModelScope.launch(topFiveError.handler) {
            val movies = movieRepository.getTopFiveMovies()
            movieList.postSuccess(movies)
        }
    }
}