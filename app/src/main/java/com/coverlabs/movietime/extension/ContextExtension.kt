package com.coverlabs.movietime.extension

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.coverlabs.movietime.R
import com.coverlabs.movietime.databinding.BottomSheetListBinding
import com.coverlabs.movietime.ui.adapter.GenreListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Context.showBottomSheetDialog(
    layoutInflater: LayoutInflater,
    title: String,
    list: List<String>,
    onClickListener: (String) -> Unit
): BottomSheetDialog {
    val binding = BottomSheetListBinding.inflate(layoutInflater)
    val bottomSheet = BottomSheetDialog(this@showBottomSheetDialog, R.style.BottomSheetDialog)

    with(binding) {
        tvTitle.text = title
        rvList.adapter = GenreListAdapter(list, onClickListener)
        val layoutManager = rvList.layoutManager as LinearLayoutManager
        if (rvList.itemDecorationCount == 0) {
            rvList.addItemDecoration(
                DividerItemDecoration(
                    this@showBottomSheetDialog,
                    layoutManager.orientation
                )
            )
        }

        bottomSheet.apply {
            setContentView(root)
            setCancelable(true)
            show()
        }
    }

    return bottomSheet
}