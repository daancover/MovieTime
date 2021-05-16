package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.coverlabs.domain.model.Movie
import com.coverlabs.movietime.databinding.FragmentHomeBinding
import com.coverlabs.movietime.extension.setupCarousel
import com.coverlabs.movietime.extension.setupSideTouchEvent
import com.coverlabs.movietime.ui.activity.MovieDetailActivity
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
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

    override fun setupView() {
        // do nothing
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
            vpTopFive.apply {
                adapter = MovieListAdapter(movieList, true) {
                    startActivity(
                        MovieDetailActivity.newIntent(requireContext(), it)
                    )
                }

                setupCarousel()
                setupSideTouchEvent()
            }
        }
    }
}