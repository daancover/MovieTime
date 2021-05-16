package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.coverlabs.domain.model.Movie
import com.coverlabs.movietime.MovieTimeApplication.Companion.GRID_LAYOUT_COLUMNS
import com.coverlabs.movietime.databinding.FragmentSearchBinding
import com.coverlabs.movietime.ui.activity.MovieDetailActivity
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
import com.coverlabs.movietime.ui.helper.GridItemDecoration
import com.coverlabs.movietime.viewmodel.SearchViewModel
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupView() {
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null

        with(binding) {
            var lengthBeforeChange = 0

            etSearch.doBeforeTextChanged { text, _, _, _ ->
                lengthBeforeChange = text?.length ?: 0
            }

            etSearch.doAfterTextChanged {
                runnable?.let { runnable ->
                    handler.removeCallbacks(runnable)
                }

                it?.let { text ->
                    runnable = Runnable {
                        if (text.length >= 2) {
                            viewModel.searchMovie(text.toString())
                        } else if (text.length < lengthBeforeChange) { // T
                            viewModel.getAllMovies()
                        }
                    }

                    runnable?.let { runnable ->
                        handler.postDelayed(runnable, SEARCH_DELAY)
                    }
                }
            }
        }
    }

    override fun observeEvents() {
        lifecycle.addObserver(viewModel)
        viewModel.onMovieListResult().observe(viewLifecycleOwner, handleMovieList())
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
            MovieDetailActivity.newIntent(requireContext(), movieId)
        )
    }

    private fun onFavoriteStatusChange(): (Boolean, Movie) -> Unit = { favorite, movie ->
        viewModel.changeFavoriteStatus(favorite, movie)
    }

    companion object {
        const val SEARCH_DELAY = 1000L
    }
}