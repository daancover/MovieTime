package com.coverlabs.movietime.ui.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.MovieDetails
import com.coverlabs.movietime.databinding.ActivityMovieDetailBinding
import com.coverlabs.movietime.extension.handleErrors
import com.coverlabs.movietime.ui.adapter.CastListAdapter
import com.coverlabs.movietime.ui.adapter.GenreListAdapter
import com.coverlabs.movietime.ui.adapter.LoadingCastListAdapter
import com.coverlabs.movietime.ui.adapter.LoadingGenreListAdapter
import com.coverlabs.movietime.ui.helper.CutImageHalf
import com.coverlabs.movietime.viewmodel.MovieDetailViewModel
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MovieDetailActivity : AppCompatActivity() {

    private val viewModel by viewModel<MovieDetailViewModel>()

    private lateinit var binding: ActivityMovieDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeEvents()
    }

    /*
    * Restore previous data to screen and update favorite status
    * */
    override fun onResume() {
        super.onResume()
        viewModel.onMovieDetailsResult().value?.data?.let {
            setupMovieDetails(it)
        }
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
                setupLoadingMovieDetails()
            }
            SUCCESS -> {
                it.dataIfNotHandled?.let { movieDetails ->
                    setupMovieDetails(movieDetails)
                }
            }
            ERROR -> {
                it.error?.let { error ->
                    handleErrors(error)
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun setupLoadingMovieDetails() {
        with(binding) {
            gbLoading.isVisible = true

            setupLoadingGenreList()

            rvCastingList.adapter = LoadingCastListAdapter(3)
        }
    }

    private fun setupMovieDetails(movieDetails: MovieDetails) {
        with(binding) {
            gbLoading.isVisible = false

            toolbar.title = movieDetails.title

            setupMoviePoster(movieDetails)

            tvDescription.text = movieDetails.overview
            cvRating.leftText = movieDetails.voteAverage.toString()
            cvRating.rightText = movieDetails.voteCount.toString()

            setupGenreList(movieDetails)

            rvCastingList.adapter = CastListAdapter(movieDetails.cast)
            tvDirectorName.text = movieDetails.director.name

            onFavoriteStatusChange(movieDetails)
        }
    }

    private fun ActivityMovieDetailBinding.setupMoviePoster(
        movieDetails: MovieDetails
    ) {
        Glide
            .with(root)
            .load(movieDetails.posterPath)
            .transform(CutImageHalf())
            .into(ivPoster)

        ivPoster.setOnClickListener {
            // TODO SHOW POSTER DIALOG
            Toast.makeText(
                this@MovieDetailActivity,
                "Poster clicked!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun ActivityMovieDetailBinding.setupLoadingGenreList() {
        rvGenreList.adapter = LoadingGenreListAdapter(3)
    }

    private fun ActivityMovieDetailBinding.setupGenreList(
        movieDetails: MovieDetails
    ) {
        rvGenreList.adapter = GenreListAdapter(movieDetails.genres) {
            val intent = GenreActivity.newIntent(this@MovieDetailActivity, it)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun ActivityMovieDetailBinding.onFavoriteStatusChange(
        movieDetails: MovieDetails
    ) {
        val movie = Movie(movieDetails.id, movieDetails.posterPath)
        cbFavorite.setOnCheckedChangeListener(null)
        cbFavorite.isChecked = viewModel.isFavorite(movie)

        cbFavorite.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeFavoriteStatus(isChecked, movie)
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
*   Optional:
*       Infinite scroll and pagination
*       Add functionality where clicking on an image preview in the first section expands the image in a modal
*
*   Top 5 movies layout
*   Search by Genre
*   Browse by all and search by "title"
*   Make SearchActivity display all movies when search length < 2
*   Order by "title" or "popularity"
*   App icon
*   Movie detail layout (title, voteAverage, voteCount, genres, posterPath, overview, cast, director)
*       Pressing a genre navigates to a new view showing the category and associated movies
*   Handle specific errors (Looks like your device is not connected to the internet. Please make sure you are connected and try again!)
*   Fix back pressed on HomeActivity and all screens that show favorite status
*   Loading
*
*
*
*       Lazy load the images of the movie item component so they only appear once the component is visible
*       Add at least one chart or graph representing anything you feel is helpful to the end user
*       Add a search bar that allows searching on 2 or more fields of the movie object
* */