package com.coverlabs.movietime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coverlabs.domain.model.MovieDetails
import com.coverlabs.domain.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieDetailViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val movieDetails = MutableLiveData<MovieDetails>()

    fun onMovieDetailsResult(): LiveData<MovieDetails> = movieDetails

    fun getMovieDetails(id: Int = 775996) {
        viewModelScope.launch {
            val details = movieRepository.getMovieDetails(id)
            movieDetails.postValue(details)
        }
    }
}