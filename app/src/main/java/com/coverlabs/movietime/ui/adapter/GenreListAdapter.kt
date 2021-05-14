package com.coverlabs.movietime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coverlabs.movietime.databinding.ItemGenreBinding

class GenreListAdapter(
    private val list: List<String>,
    private val onClickListener: (String) -> Unit
) : RecyclerView.Adapter<GenreListAdapter.GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemGenreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(
            list[position],
            onClickListener
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class GenreViewHolder(
        private val binding: ItemGenreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            genre: String,
            onClickListener: (String) -> Unit
        ) {
            with(binding) {
                tvGenre.text = genre

                root.setOnClickListener {
                    onClickListener(genre)
                }
            }
        }
    }
}