package com.coverlabs.movietime.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.coverlabs.domain.model.Movie
import com.coverlabs.movietime.MovieTimeApplication.Companion.GRID_LAYOUT_COLUMNS
import com.coverlabs.movietime.databinding.ActivityGenreBinding
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
import com.coverlabs.movietime.ui.decoration.MovieListItemDecoration
import com.coverlabs.movietime.viewmodel.GenreViewModel
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GenreActivity : AppCompatActivity() {

    private val viewModel by viewModel<GenreViewModel>()

    private lateinit var binding: ActivityGenreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeEvents()
    }

    private fun observeEvents() {
        lifecycle.addObserver(viewModel)
        val genre = intent.getStringExtra(ARG_GENRE).orEmpty()
        viewModel.onMovieListResult().observe(this, handleMovieList())
        viewModel.searchMovie(genre)
    }

    private fun handleMovieList() = Observer<State<List<Movie>>> {
        when (it.status) {
            LOADING -> {
                // TODO LOADING
            }
            SUCCESS -> {
                it.dataIfNotHandled?.let { movieList ->
                    setupMovieList(movieList)
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

    private fun setupMovieList(movieList: List<Movie>) {
        with(binding) {
            val context = this@GenreActivity
            rvMovieList.layoutManager = GridLayoutManager(context, GRID_LAYOUT_COLUMNS)

            if (rvMovieList.itemDecorationCount == 0) {
                rvMovieList.addItemDecoration(MovieListItemDecoration())
            }

            rvMovieList.adapter = MovieListAdapter(movieList, false) {
                startActivity(
                    MovieDetailActivity.newIntent(context, it)
                )
            }
        }
    }

    companion object {
        private const val ARG_GENRE = "arg_genre"

        fun newIntent(context: Context, genre: String) = Intent(
            context,
            GenreActivity::class.java
        ).apply {
            putExtra(ARG_GENRE, genre)
        }
    }
}