package com.coverlabs.movietime.ui.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.MovieDetails
import com.coverlabs.movietime.databinding.ActivityMovieDetailBinding
import com.coverlabs.movietime.extension.handleError
import com.coverlabs.movietime.ui.adapter.CastListAdapter
import com.coverlabs.movietime.ui.adapter.GenreListAdapter
import com.coverlabs.movietime.ui.adapter.LoadingCastListAdapter
import com.coverlabs.movietime.ui.adapter.LoadingGenreListAdapter
import com.coverlabs.movietime.ui.dialog.MoviePosterDialog
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
                    handleError(error)
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
            movieDetails.posterPath?.let {
                val dialog = MoviePosterDialog(it)
                dialog.show(supportFragmentManager, "MoviePosterDialog")
            }
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