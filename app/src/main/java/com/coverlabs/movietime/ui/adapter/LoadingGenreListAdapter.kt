package com.coverlabs.movietime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coverlabs.movietime.databinding.ItemGenreLoadingBinding

class LoadingGenreListAdapter(
    private val size: Int
) : RecyclerView.Adapter<LoadingGenreListAdapter.LoadingGenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingGenreViewHolder {
        val binding = ItemGenreLoadingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LoadingGenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadingGenreViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return size
    }

    class LoadingGenreViewHolder(
        binding: ItemGenreLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root)
}