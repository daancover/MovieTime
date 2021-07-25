package com.coverlabs.movietime.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextWatcher
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coverlabs.domain.model.Movie
import com.coverlabs.domain.model.OrderBy
import com.coverlabs.domain.model.OrderBy.*
import com.coverlabs.domain.model.Sort
import com.coverlabs.domain.model.Sort.ASC
import com.coverlabs.domain.model.Sort.DESC
import com.coverlabs.movietime.MovieTimeApplication.Companion.GRID_LAYOUT_COLUMNS
import com.coverlabs.movietime.R
import com.coverlabs.movietime.databinding.FragmentSearchBinding
import com.coverlabs.movietime.extension.handleError
import com.coverlabs.movietime.extension.setSupportActionBar
import com.coverlabs.movietime.extension.showBottomSheetDialog
import com.coverlabs.movietime.ui.activity.MovieDetailActivity
import com.coverlabs.movietime.ui.adapter.LoadingMovieListAdapter
import com.coverlabs.movietime.ui.adapter.MovieListAdapter
import com.coverlabs.movietime.ui.helper.GridItemDecoration
import com.coverlabs.movietime.ui.helper.InfiniteScrollListener
import com.coverlabs.movietime.viewmodel.SearchViewModel
import com.coverlabs.movietime.viewmodel.SearchViewModel.Companion.SEARCH_LIMIT
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

    /*
    * Restore previous data to screen and update favorite status
    * */
    override fun onResume() {
        super.onResume()
        viewModel.onMovieListResult().value?.data?.let {
            setupMovieList(it)
        }
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

    override fun observeEvents() {
        lifecycle.addObserver(viewModel)
        viewModel.onMovieListResult().observe(viewLifecycleOwner, handleMovieList())
        viewModel.onMovieListAddedResult().observe(viewLifecycleOwner, handleMovieListAdded())
        viewModel.onGenreListResult().observe(viewLifecycleOwner, handleGenreList())
    }

    private fun FragmentSearchBinding.onExpandClickListener() {
        cbExpand.setOnCheckedChangeListener { _, isChecked ->
            etGenre.setText("")
            gpGenre.isVisible = isChecked
        }
    }

    private fun FragmentSearchBinding.onSearchClickListener() {
        btSearch.setOnClickListener {
            viewModel.searchMovie(etSearch.text.toString(), etGenre.text.toString())
        }
    }

    private fun FragmentSearchBinding.onGenreListClickListener() {
        etGenre.setOnClickListener {
            if (!pbGenre.isVisible) {
                viewModel.getGenreList()
            }
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

    private fun search(orderBy: OrderBy, sort: Sort) {
        with(binding) {
            viewModel.updateSortingPreferences(orderBy, sort)
            viewModel.searchMovie(etSearch.text.toString(), etGenre.text.toString())
        }
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
                    requireContext().handleError(error)
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun handleMovieListAdded() = Observer<State<List<Movie>>> {
        when (it.status) {
            LOADING -> {
                showInfiniteScrollLoading(true)
            }
            SUCCESS -> {
                it.dataIfNotHandled?.let { movieList ->
                    addMovies(movieList)
                }
            }
            ERROR -> {
                showInfiniteScrollLoading(false)
                it.error?.let { error ->
                    requireContext().handleError(error)
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun showInfiniteScrollLoading(visible: Boolean) {
        binding.pbInfiniteScroll.isVisible = visible
    }

    private fun handleGenreList() = Observer<State<List<String>>> {
        when (it.status) {
            LOADING -> {
                binding.pbGenre.isVisible = true
            }
            SUCCESS -> {
                binding.pbGenre.isVisible = false
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
                binding.pbGenre.isVisible = false
                it.error?.let { error ->
                    requireContext().handleError(error)
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun setupMovieList(movieList: List<Movie>) {
        with(binding) {
            if (movieList.isNotEmpty()) {
                val gridLayoutManager = GridLayoutManager(context, GRID_LAYOUT_COLUMNS)
                rvMovieList.layoutManager = gridLayoutManager

                if (rvMovieList.itemDecorationCount == 0) {
                    rvMovieList.addItemDecoration(GridItemDecoration())
                }

                rvMovieList.clearOnScrollListeners()
                rvMovieList.addOnScrollListener(createInfiniteScrollListener(gridLayoutManager))

                val isFavoriteList = viewModel.isFavoriteList(movieList)
                rvMovieList.adapter = MovieListAdapter(
                    movieList.toMutableList(),
                    isFavoriteList.toMutableList(),
                    false,
                    onClickListener = onMovieClickListener(),
                    onFavoriteStatusChange = onFavoriteStatusChange()
                )

                gpNoMovie.isVisible = false
                rvMovieList.isVisible = true
            } else {
                gpNoMovie.isVisible = true
                rvMovieList.isVisible = false
            }
        }
    }

    private fun addMovies(movieList: List<Movie>) {
        with(binding) {
            showInfiniteScrollLoading(false)
            val isFavoriteList = viewModel.isFavoriteList(movieList)
            (rvMovieList.adapter as? MovieListAdapter)?.addMovies(movieList, isFavoriteList)
        }
    }

    private fun setupLoadingMovieList() {
        with(binding) {
            rvMovieList.layoutManager = GridLayoutManager(context, GRID_LAYOUT_COLUMNS)

            if (rvMovieList.itemDecorationCount == 0) {
                rvMovieList.addItemDecoration(GridItemDecoration())
            }

            rvMovieList.adapter = LoadingMovieListAdapter(
                10,
                false
            )

            gpNoMovie.isVisible = false
            rvMovieList.isVisible = true
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

    private fun createInfiniteScrollListener(gridLayoutManager: GridLayoutManager) =
        object : InfiniteScrollListener(gridLayoutManager) {
            override fun onLoadMore(
                currentPage: Int,
                totalItemCount: Int,
                recyclerView: RecyclerView
            ) {
                if (currentPage > 0) {
                    with(binding) {
                        viewModel.fetchMoreMovies(
                            etSearch.text.toString(),
                            etGenre.text.toString(),
                            SEARCH_LIMIT * currentPage
                        )
                    }
                }
            }
        }

    companion object {
        const val SEARCH_DELAY = 1000L
    }
}