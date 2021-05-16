package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.viewModelScope
import com.coverlabs.di.error.ErrorHandler
import com.coverlabs.domain.model.MovieDetails
import com.coverlabs.domain.repository.MovieRepository
import com.coverlabs.movietime.viewmodel.base.BaseViewModel
import com.coverlabs.movietime.viewmodel.base.StateMutableLiveData
import kotlinx.coroutines.launch

class MovieDetailViewModel(private val movieRepository: MovieRepository) : BaseViewModel() {

    private val movieDetails = StateMutableLiveData<MovieDetails>()

    private val movieDetailsError = ErrorHandler { error ->
        movieDetails.postError(error)
    }

    fun onMovieDetailsResult() = movieDetails.toLiveData()

    fun getMovieDetails(id: Int) {
        viewModelScope.launch(movieDetailsError.handler) {
            movieDetails.postLoading()
            val details = movieRepository.getMovieDetails(id)
            movieDetails.postSuccess(details)
        }
    }
}