package com.coverlabs.movietime.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.OrderBy
import com.coverlabs.domain.model.Sort
import com.coverlabs.movietime.MovieTimeApplication.Companion.GRID_LAYOUT_COLUMNS
import com.coverlabs.movietime.R
import com.coverlabs.movietime.databinding.ActivityGenreBinding
import com.coverlabs.movietime.extension.handleError
import com.coverlabs.movietime.ui.adapter.LoadingMovieListAdapter
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
import com.coverlabs.movietime.ui.helper.GridItemDecoration
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
        setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_title -> {
                search(OrderBy.TITLE, Sort.ASC)
            }
            R.id.sort_popularity -> {
                search(OrderBy.POPULARITY, Sort.DESC)
            }
            R.id.sort_newer -> {
                search(OrderBy.RELEASE_DATE, Sort.DESC)
            }
            R.id.sort_older -> {
                search(OrderBy.RELEASE_DATE, Sort.ASC)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*
    * Restore previous data to screen and update favorite status
    * */
    override fun onResume() {
        super.onResume()
        viewModel.onMovieListResult().value?.data?.let {
            setupMovieList(it)
        }
    }

    private fun observeEvents() {
        lifecycle.addObserver(viewModel)
        val genre = intent.getStringExtra(ARG_GENRE).orEmpty()
        viewModel.onMovieListResult().observe(this, handleMovieList())
        viewModel.searchMovie(genre)
    }

    private fun setupToolbar() {
        with(binding) {
            val genre = intent.getStringExtra(ARG_GENRE).orEmpty()
            setSupportActionBar(toolbar)
            supportActionBar?.title = genre
            toolbar.overflowIcon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_sort,
                null
            )
        }
    }

    private fun search(orderBy: OrderBy, sort: Sort) {
        val genre = intent.getStringExtra(ARG_GENRE).orEmpty()
        viewModel.updateSortingPreferences(orderBy, sort)
        viewModel.searchMovie(genre)
    }

    private fun handleMovieList() = Observer<State<List<Movie>>> {
        when (it.status) {
            LOADING -> {
                setupLoadingMovieList()
            }
            SUCCESS -> {
                it.dataIfNotHandled?.let { movieList ->
                    setupMovieList(movieList)
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

    private fun setupLoadingMovieList() {
        with(binding) {
            val context = this@GenreActivity
            rvMovieList.layoutManager = GridLayoutManager(context, GRID_LAYOUT_COLUMNS)

            if (rvMovieList.itemDecorationCount == 0) {
                rvMovieList.addItemDecoration(GridItemDecoration())
            }

            rvMovieList.adapter = LoadingMovieListAdapter(
                10,
                false
            )
        }
    }

    private fun setupMovieList(movieList: List<Movie>) {
        with(binding) {
            val context = this@GenreActivity
            rvMovieList.layoutManager = GridLayoutManager(context, GRID_LAYOUT_COLUMNS)

            if (rvMovieList.itemDecorationCount == 0) {
                rvMovieList.addItemDecoration(GridItemDecoration())
            }

            val isFavoriteList = viewModel.isFavoriteList(movieList)
            rvMovieList.adapter = MovieListAdapter(
                movieList.toMutableList(),
                isFavoriteList.toMutableList(),
                false,
                onClickListener = onMovieClickListener(),
                onFavoriteStatusChange = onFavoriteStatusChange()
            )
        }
    }

    private fun onMovieClickListener(): (Int) -> Unit = {
        navigateToMovieDetails(it)
    }

    private fun navigateToMovieDetails(movieId: Int) {
        startActivity(
            MovieDetailActivity.newIntent(this, movieId)
        )
    }

    private fun onFavoriteStatusChange(): (Boolean, Movie) -> Unit = { favorite, movie ->
        viewModel.changeFavoriteStatus(favorite, movie)
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