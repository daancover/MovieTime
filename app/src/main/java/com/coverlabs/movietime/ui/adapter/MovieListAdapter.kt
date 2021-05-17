package com.coverlabs.movietime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coverlabs.domain.model.Movie
import com.coverlabs.movietime.R
import com.coverlabs.movietime.databinding.ItemMovieBinding

class MovieListAdapter(
    private val list: MutableList<Movie>,
    private val isFavorite: MutableList<Boolean>,
    private val isPagerAdapter: Boolean,
    private val onClickListener: (Int) -> Unit,
    private val onFavoriteStatusChange: (Boolean, Movie) -> Unit
) : RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        if (isPagerAdapter) {
            binding.root.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }

        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(
            list[position],
            isFavorite[position],
            onClickListener,
            onFavoriteStatusChange
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateFavoriteStatus(isFavorite: Boolean, movie: Movie) {
        getMovieIndex(movie).takeIf { index -> index >= 0 }?.let { index ->
            this.isFavorite[index] = isFavorite
            notifyItemChanged(index)
        }
    }

    fun changeFavoriteStatus(isFavorite: Boolean, movie: Movie) {
        if (isFavorite) {
            addItem(movie)
        } else {
            removeItem(movie)
        }
    }

    private fun addItem(movie: Movie) {
        list.add(movie)
        isFavorite.add(true)
        notifyItemInserted(list.size - 1)
    }

    private fun removeItem(movie: Movie) {
        getMovieIndex(movie).takeIf { index -> index >= 0 }?.let { index ->
            list.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private fun getMovieIndex(movie: Movie): Int {
        var indexToRemove: Int = -1
        list.forEachIndexed { index, it ->
            if (it.id == movie.id) {
                indexToRemove = index
            }
        }
        return indexToRemove
    }

    fun addMovies(movieList: List<Movie>, favoriteList: List<Boolean>) {
        val firstIndex = list.size
        movieList.forEachIndexed { index, movie ->
            list.add(movie)
            isFavorite.add(favoriteList[index])
        }

        notifyItemRangeInserted(firstIndex, movieList.size)
    }

    class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            movie: Movie,
            isFavorite: Boolean,
            onClickListener: (Int) -> Unit,
            onFavoriteStatusChange: (Boolean, Movie) -> Unit
        ) {
            with(binding) {
                Glide
                    .with(root)
                    .load(movie.posterPath)
                    .centerCrop()
                    .placeholder(R.drawable.img_movie_placeholder)
                    .into(ivPoster)

                root.setOnClickListener {
                    onClickListener(movie.id)
                }

                cbFavorite.setOnCheckedChangeListener(null)
                cbFavorite.isChecked = isFavorite
                cbFavorite.setOnCheckedChangeListener { _, isChecked ->
                    onFavoriteStatusChange(isChecked, movie)
                }
            }
        }
    }
}