package com.coverlabs.movietime.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.coverlabs.domain.model.MovieDetails
import com.coverlabs.movietime.databinding.ActivityMovieDetailBinding
import com.coverlabs.movietime.viewmodel.MovieDetailViewModel
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieDetailActivity : AppCompatActivity() {

    private val viewModel by viewModel<MovieDetailViewModel>()

    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeEvents()
    }

    private fun observeEvents() {
        lifecycle.addObserver(viewModel)
        val movieId = intent.getIntExtra(ARGS_MOVIE_ID, 0)
        viewModel.onMovieDetailsResult().observe(this, handleMovieDetails())
        viewModel.getMovieDetails(movieId)
    }

    private fun handleMovieDetails() = Observer<State<MovieDetails>> {
        when (it.status) {
            LOADING -> {
                // TODO LOADING
            }
            SUCCESS -> {
                it.dataIfNotHandled?.let { movieDetails ->
                    setupMovieDetails(movieDetails)
                }
            }
            ERROR -> {
                // TODO ERROR
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun setupMovieDetails(movieDetails: MovieDetails) {
        with(binding) {
            Glide
                .with(root)
                .load(movieDetails.posterPath)
                .into(ivPoster)
        }
    }

    companion object {
        private const val ARGS_MOVIE_ID = "args_movie_id"

        fun newIntent(context: Context, movieId: Int) = Intent(
            context,
            MovieDetailActivity::class.java
        ).apply {
            putExtra(ARGS_MOVIE_ID, movieId)
        }
    }
}

/*
* TODO LIST
*   Top 5 movies layout
*   Search by Genre
*   Browse by all and search by "title"
*   Make SearchActivity display all movies when search length < 3
*   Order by "title" or "voteAverage"
*   Movie detail layout (title, voteAverage, voteCount, genres, posterPath, overview, cast, director)
*       Pressing a genre navigates to a new view showing the category and associated movies
*   Handle specific errors
*   Optional:
*       Infinite scroll and pagination
*       Add a search bar that allows searching on 2 or more fields of the movie object
*
*       Add functionality where clicking on an image preview in the first section expands the image in a modal
*       Lazy load the images of the movie item component so they only appear once the component is visible
*       Add at least one chart or graph representing anything you feel is helpful to the end user
* */