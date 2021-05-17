package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextWatcher
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.OrderBy
import com.coverlabs.domain.model.OrderBy.*
import com.coverlabs.domain.model.Sort
import com.coverlabs.domain.model.Sort.ASC
import com.coverlabs.domain.model.Sort.DESC
import com.coverlabs.movietime.MovieTimeApplication.Companion.GRID_LAYOUT_COLUMNS
import com.coverlabs.movietime.R
import com.coverlabs.movietime.databinding.FragmentSearchBinding
import com.coverlabs.movietime.extension.setSupportActionBar
import com.coverlabs.movietime.extension.showBottomSheetDialog
import com.coverlabs.movietime.ui.activity.MovieDetailActivity
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
import com.coverlabs.movietime.ui.helper.GridItemDecoration
import com.coverlabs.movietime.viewmodel.SearchViewModel
import com.coverlabs.movietime.viewmodel.base.State
import com.coverlabs.movietime.viewmodel.base.State.Status.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private var bottomSheetDialog: BottomSheetDialog? = null

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupToolbar()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_title -> {
                search(TITLE, ASC)
            }
            R.id.sort_popularity -> {
                search(POPULARITY, DESC)
            }
            R.id.sort_newer -> {
                search(RELEASE_DATE, DESC)
            }
            R.id.sort_older -> {
                search(RELEASE_DATE, ASC)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        with(binding) {
            setHasOptionsMenu(true)
            activity?.setSupportActionBar(toolbar)
            toolbar.title = ""
            toolbar.overflowIcon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_sort,
                null
            )
        }
    }

    override fun setupView() {
        with(binding) {
            onExpandClickListener()
            onSearchClickListener()
            onGenreListClickListener()
            onSearchTextChanged()
        }
    }

    private fun FragmentSearchBinding.onExpandClickListener() {
        cbExpand.setOnCheckedChangeListener { _, isChecked ->
            etGenre.setText("")

            if (isChecked) {
                gpGenre.visibility = VISIBLE
            } else {
                gpGenre.visibility = GONE
            }
        }
    }

    private fun FragmentSearchBinding.onSearchClickListener() {
        btSearch.setOnClickListener {
            viewModel.searchMovie(etSearch.text.toString(), etGenre.text.toString())
        }
    }

    private fun FragmentSearchBinding.onGenreListClickListener() {
        etGenre.setOnClickListener {
            // TODO CHECK IF IT'S NOT LOADING
            viewModel.getGenreList()
        }
    }

    private fun FragmentSearchBinding.onSearchTextChanged(): TextWatcher {
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null

        var lengthBeforeChange = 0

        etSearch.doBeforeTextChanged { text, _, _, _ ->
            lengthBeforeChange = text?.length ?: 0
        }

        return etSearch.doAfterTextChanged {
            runnable?.let { runnable ->
                handler.removeCallbacks(runnable)
            }

            it?.let { text ->
                runnable = Runnable {
                    if (text.length >= 2) {
                        viewModel.searchMovie(text.toString(), etGenre.text.toString())
                    } else if (text.length < lengthBeforeChange) {
                        viewModel.searchMovie(genre = etGenre.text.toString())
                    }
                }

                runnable?.let { runnable ->
                    handler.postDelayed(runnable, SEARCH_DELAY)
                }
            }
        }
    }

    override fun observeEvents() {
        lifecycle.addObserver(viewModel)
        viewModel.onMovieListResult().observe(viewLifecycleOwner, handleMovieList())
        viewModel.onGenreListResult().observe(viewLifecycleOwner, handleGenreList())
    }

    private fun search(orderBy: OrderBy, sort: Sort) {
        with(binding) {
            viewModel.updateSortingPreferences(orderBy, sort)
            viewModel.searchMovie(etSearch.text.toString(), etGenre.text.toString())
        }
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

    private fun handleGenreList() = Observer<State<List<String>>> {
        when (it.status) {
            LOADING -> {
                // TODO LOADING
            }
            SUCCESS -> {
                it.dataIfNotHandled?.let { genreList ->
                    if (bottomSheetDialog == null) {
                        bottomSheetDialog = context?.showBottomSheetDialog(
                            layoutInflater,
                            getString(R.string.search_select_genre_title),
                            genreList
                        ) { genre ->
                            binding.etGenre.setText(genre)
                            bottomSheetDialog?.dismiss()
                        }

                        bottomSheetDialog?.setOnDismissListener {
                            bottomSheetDialog = null
                        }
                    }
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