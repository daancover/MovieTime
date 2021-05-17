package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.viewModelScope
import com.coverlabs.di.error.ErrorHandler
import com.coverlabs.domain.model.MovieDetails
import com.coverlabs.domain.repository.MovieRepository
import com.coverlabs.domain.repository.StorageRepository
import com.coverlabs.movietime.viewmodel.base.StateMutableLiveData
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieRepository: MovieRepository,
    storageRepository: StorageRepository
) : MovieListViewModel(storageRepository) {

    private val movieDetails = StateMutableLiveData<MovieDetails>()

    private val movieDetailsError = ErrorHandler { error ->
        movieDetails.postError(error)
    }

    fun onMovieDetailsResult() = movieDetails.toLiveData()

    fun getMovieDetails(id: Int) {
        movieDetails.postLoading()
        viewModelScope.launch(movieDetailsError.handler) {
            val details = movieRepository.getMovieDetails(id)
            movieDetails.postSuccess(details)
        }
    }
}