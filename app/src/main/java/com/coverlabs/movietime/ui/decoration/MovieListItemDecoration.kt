package com.coverlabs.movietime.ui.decoration

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.coverlabs.movietime.extension.dp

class MovieListItemDecoration(private val resources: Resources) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)

        outRect.top = 8.dp(resources)
        outRect.bottom = 8.dp(resources)

        if (position % 2 == 0) {
            outRect.left = 16.dp(resources)
            outRect.right = 8.dp(resources)
        } else {
            outRect.left = 8.dp(resources)
            outRect.right = 16.dp(resources)
        }
    }
}