package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.coverlabs.domain.model.Movie
import com.coverlabs.movietime.databinding.FragmentHomeBinding
import com.coverlabs.movietime.extension.handleErrors
import com.coverlabs.movietime.extension.setupCarousel
import com.coverlabs.movietime.extension.setupSideTouchEvent
import com.coverlabs.movietime.ui.activity.MovieDetailActivity
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
import com.coverlabs.movietime.ui.helper.HorizontalListItemDecoration
import com.coverlabs.movietime.viewmodel.HomeViewModel
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /*
    * Restore previous data to screen and update favorite status
    * */
    override fun onResume() {
        super.onResume()
        viewModel.onMovieListResult().value?.data?.let {
            setupMovieList(it)
            viewModel.getFavorites()
        }
    }

    override fun setupView() {
        // do nothing
    }

    override fun observeEvents() {
        lifecycle.addObserver(viewModel)
        viewModel.onMovieListResult().observe(viewLifecycleOwner, handleMovieList())
        viewModel.onFavoriteMovieListResult().observe(viewLifecycleOwner, handleFavoriteMovieList())
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
                it.error?.let { error ->
                    requireContext().handleErrors(error)
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun handleFavoriteMovieList() = Observer<State<List<Movie>>> {
        when (it.status) {
            SUCCESS -> {
                it.dataIfNotHandled?.let { movieList ->
                    setupFavoriteMovieList(movieList)
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun setupMovieList(movieList: List<Movie>) {
        with(binding) {
            vpTopFive.apply {
                val isFavoriteList = viewModel.isFavoriteList(movieList)
                adapter = MovieListAdapter(
                    movieList.toMutableList(),
                    isFavoriteList.toMutableList(),
                    true,
                    onClickListener = onMovieClickListener(),
                    onFavoriteStatusChange = onFavoriteStatusChange()
                )

                setupCarousel()
                setupSideTouchEvent()
            }
        }
    }

    private fun setupFavoriteMovieList(movieList: List<Movie>) {
        with(binding) {
            val layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
            rvFavorites.layoutManager = layoutManager

            if (rvFavorites.itemDecorationCount == 0) {
                rvFavorites.addItemDecoration(HorizontalListItemDecoration())
            }

            val isFavoriteList = viewModel.isFavoriteList(movieList)
            rvFavorites.adapter =
                MovieListAdapter(
                    movieList.toMutableList(),
                    isFavoriteList.toMutableList(),
                    false,
                    onClickListener = onMovieClickListener(),
                    onFavoriteStatusChange = onFavoriteStatusChange()
                )

            if (movieList.isNotEmpty()) {
                gpNoFavorite.isVisible = false
                rvFavorites.isVisible = true
            } else {
                gpNoFavorite.isVisible = true
                rvFavorites.isVisible = false
            }
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

    private fun FragmentHomeBinding.onFavoriteStatusChange(): (Boolean, Movie) -> Unit =
        { favorite, movie ->
            viewModel.changeFavoriteStatus(favorite, movie)
            root.post {
                (vpTopFive.adapter as? MovieListAdapter)?.updateFavoriteStatus(favorite, movie)
                (rvFavorites.adapter as? MovieListAdapter)?.apply {
                    changeFavoriteStatus(favorite, movie)

                    if (itemCount > 0) {
                        gpNoFavorite.isVisible = false
                        rvFavorites.isVisible = true
                    } else {
                        gpNoFavorite.isVisible = true
                        rvFavorites.isVisible = false
                    }
                }
            }
        }
}