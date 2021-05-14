package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.coverlabs.domain.model.Movie
import com.coverlabs.movietime.MovieTimeApplication.Companion.GRID_LAYOUT_COLUMNS
import com.coverlabs.movietime.databinding.FragmentSearchBinding
import com.coverlabs.movietime.ui.activity.MovieDetailActivity
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
import com.coverlabs.movietime.ui.decoration.MovieListItemDecoration
import com.coverlabs.movietime.viewmodel.SearchViewModel
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
        with(binding) {
            etSearch.doAfterTextChanged {
                it?.let { text ->
                    if (text.length >= 3) {
                        viewModel.searchMovie(it.toString())
                    }
                }
            }
        }
    }

    override fun observeEvents() {
        viewModel.onMovieListResult().observe(viewLifecycleOwner, handleMovieList())
    }

    private fun handleMovieList() = Observer<List<Movie>> {
        setupMovieList(it)
    }

    private fun setupMovieList(movieList: List<Movie>) {
        with(binding) {
            rvMovieList.layoutManager = GridLayoutManager(context, GRID_LAYOUT_COLUMNS)

            if (rvMovieList.itemDecorationCount == 0) {
                rvMovieList.addItemDecoration(MovieListItemDecoration(resources))
            }

            rvMovieList.adapter = MovieListAdapter(movieList) {
                startActivity(
                    MovieDetailActivity.newIntent(requireContext(), it)
                )
            }
        }
    }
}