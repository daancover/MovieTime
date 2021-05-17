package com.coverlabs.movietime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coverlabs.movietime.databinding.ItemCastLoadingBinding

class LoadingCastListAdapter(
    private val size: Int
) : RecyclerView.Adapter<LoadingCastListAdapter.LoadingCastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingCastViewHolder {
        val binding = ItemCastLoadingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LoadingCastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadingCastViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return size
    }

    class LoadingCastViewHolder(
        binding: ItemCastLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root)
}