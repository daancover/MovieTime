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
    private val list: List<Movie>,
    private val isPagerAdapter: Boolean,
    private val onClickListener: (Int) -> Unit
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
            onClickListener
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            movie: Movie,
            onClickListener: (Int) -> Unit
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
            }
        }
    }
}