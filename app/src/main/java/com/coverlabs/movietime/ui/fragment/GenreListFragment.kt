package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.coverlabs.movietime.databinding.FragmentGenreBinding
import com.coverlabs.movietime.ui.activity.GenreActivity
import com.coverlabs.movietime.ui.adapter.GenreListAdapter
import com.coverlabs.movietime.viewmodel.GenreListViewModel
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
        viewModel.onGenreListResult().observe(viewLifecycleOwner, handleGenreList())
        viewModel.getGenreList()
    }

    private fun handleGenreList() = Observer<List<String>> {
        setupGenreList(it)
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