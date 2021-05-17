package com.coverlabs.movietime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coverlabs.domain.model.Cast
import com.coverlabs.movietime.databinding.ItemCastBinding

class CastListAdapter(
    private val list: List<Cast>
) : RecyclerView.Adapter<CastListAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(
            list[position]
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CastViewHolder(
        private val binding: ItemCastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            cast: Cast
        ) {
            with(binding) {
                tvName.text = cast.name
                tvCharacter.text = cast.character
            }
        }
    }
}