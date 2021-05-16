package com.coverlabs.movietime.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.coverlabs.movietime.extension.dp

class MovieListItemDecoration() : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)

        outRect.top = 8.dp()
        outRect.bottom = 8.dp()

        if (position % 2 == 0) {
            outRect.left = 16.dp()
            outRect.right = 8.dp()
        } else {
            outRect.left = 8.dp()
            outRect.right = 16.dp()
        }
    }
}