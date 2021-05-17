package com.coverlabs.movietime.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.coverlabs.movietime.databinding.DialogMoviePosterBinding

class MoviePosterDialog(private val imageUrl: String) : DialogFragment() {

    private lateinit var binding: DialogMoviePosterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMoviePosterBinding.inflate(inflater, container, false)
        isCancelable = true

        Glide
            .with(this)
            .load(imageUrl)
            .into(binding.ivPoster)

        return binding.root
    }
}