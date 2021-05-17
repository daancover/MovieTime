package com.coverlabs.movietime.extension

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.coverlabs.di.error.Error
import com.coverlabs.di.error.Error.DeviceNotConnected
import com.coverlabs.di.error.Error.GenericError
import com.coverlabs.movietime.R
import com.coverlabs.movietime.databinding.BottomSheetListBinding
import com.coverlabs.movietime.ui.adapter.GenreListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Context.showDialog(
    @StringRes title: Int = R.string.generic_error_title,
    @StringRes message: Int = R.string.generic_error_message,
    @StringRes positiveButtonText: Int = R.string.generic_error_button,
    positiveButtonClick: (dialog: DialogInterface, which: Int) -> Unit = { _, _ -> },
    @StringRes negativeButtonText: Int? = null,
    negativeButtonClick: (dialog: DialogInterface, which: Int) -> Unit = { _, _ -> }
) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)

        setPositiveButton(positiveButtonText, positiveButtonClick)

        negativeButtonText?.let {
            setNegativeButton(it, negativeButtonClick)
        }
        setCancelable(false)
        create()
        show()
    }
}

fun Context.handleErrors(error: Error): Boolean {
    return when (error) {
        is DeviceNotConnected -> {
            showDialog(message = R.string.device_not_connected_message)
            true
        }
        is GenericError -> {
            showDialog()
            true
        }
        else -> {
            false
        }
    }
}

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