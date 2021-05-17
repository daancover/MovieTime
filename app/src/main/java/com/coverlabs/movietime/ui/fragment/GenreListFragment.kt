package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.coverlabs.movietime.databinding.FragmentGenreBinding
import com.coverlabs.movietime.extension.handleErrors
import com.coverlabs.movietime.ui.activity.GenreActivity
import com.coverlabs.movietime.ui.adapter.GenreListAdapter
import com.coverlabs.movietime.ui.adapter.LoadingGenreListAdapter
import com.coverlabs.movietime.viewmodel.GenreListViewModel
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GenreListFragment : BaseFragment() {

    private val viewModel by viewModel<GenreListViewModel>()

    private lateinit var binding: FragmentGenreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupView() {
        // do nothing
    }

    override fun observeEvents() {
        lifecycle.addObserver(viewModel)
        viewModel.onGenreListResult().observe(viewLifecycleOwner, handleGenreList())
    }

    private fun handleGenreList() = Observer<State<List<String>>> {
        when (it.status) {
            LOADING -> {
                setupLoadingGenreList()
            }
            SUCCESS -> {
                it.dataIfNotHandled?.let { genreList ->
                    setupGenreList(genreList)
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

    private fun setupLoadingGenreList() {
        with(binding) {
            val layoutManager = rvGenreList.layoutManager as LinearLayoutManager
            if (rvGenreList.itemDecorationCount == 0) {
                rvGenreList.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        layoutManager.orientation
                    )
                )
            }

            rvGenreList.adapter = LoadingGenreListAdapter(10)
        }
    }

    private fun setupGenreList(genreList: List<String>) {
        with(binding) {
            val layoutManager = rvGenreList.layoutManager as LinearLayoutManager
            if (rvGenreList.itemDecorationCount == 0) {
                rvGenreList.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        layoutManager.orientation
                    )
                )
            }

            rvGenreList.adapter = GenreListAdapter(genreList) {
                startActivity(
                    GenreActivity.newIntent(requireContext(), it)
                )
            }
        }
    }
}