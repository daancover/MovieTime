package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.coverlabs.domain.model.Movie
import com.coverlabs.movietime.MovieTimeApplication.Companion.GRID_LAYOUT_COLUMNS
import com.coverlabs.movietime.databinding.FragmentHomeBinding
import com.coverlabs.movietime.ui.activity.MovieDetailActivity
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
import com.coverlabs.movietime.ui.decoration.MovieListItemDecoration
import com.coverlabs.movietime.viewmodel.HomeViewModel
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
        viewModel.onMovieListResult().observe(viewLifecycleOwner, handleMovieList())
        viewModel.getTopFiveMovies()
    }

    private fun handleMovieList() = Observer<List<Movie>> {
        setupMovieList(it)
    }

    private fun setupMovieList(movieList: List<Movie>) {
        with(binding) {
            rvTopFive.layoutManager = GridLayoutManager(context, GRID_LAYOUT_COLUMNS)

            if (rvTopFive.itemDecorationCount == 0) {
                rvTopFive.addItemDecoration(MovieListItemDecoration(resources))
            }

            rvTopFive.adapter = MovieListAdapter(movieList) {
                startActivity(
                    MovieDetailActivity.newIntent(requireContext(), it)
                )
            }
        }
    }
}