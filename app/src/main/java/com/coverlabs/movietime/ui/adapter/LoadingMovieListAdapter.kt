package com.coverlabs.movietime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView
import com.coverlabs.movietime.databinding.ItemMovieLoadingBinding

class LoadingMovieListAdapter(
    private val size: Int,
    private val isPagerAdapter: Boolean
) : RecyclerView.Adapter<LoadingMovieListAdapter.LoadingMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingMovieViewHolder {
        val binding = ItemMovieLoadingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        if (isPagerAdapter) {
            binding.root.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }

        return LoadingMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadingMovieViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return size
    }

    class LoadingMovieViewHolder(
        binding: ItemMovieLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root)
}