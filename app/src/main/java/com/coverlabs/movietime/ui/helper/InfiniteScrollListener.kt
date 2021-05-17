package com.coverlabs.movietime.ui.helper

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


abstract class InfiniteScrollListener(
    private val layoutManager: RecyclerView.LayoutManager
) : RecyclerView.OnScrollListener() {
    private var currentPage = 0
    private var loading = true
    private var previousTotalItemCount = 0
    private val startingPageIndex = 0
    private val visibleThreshold = 3

    abstract fun onLoadMore(currentPage: Int, totalItemCount: Int, recyclerView: RecyclerView)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        var lastVisibleItemPosition = 0
        val totalItemCount = layoutManager.itemCount

        when (layoutManager) {
            is StaggeredGridLayoutManager -> {
                lastVisibleItemPosition = getLastVisibleItem(
                    layoutManager.findLastVisibleItemPositions(null)
                )
            }
            is GridLayoutManager -> {
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            }
        }

        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount

            if (totalItemCount == 0) {
                loading = true
            }
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && visibleThreshold + lastVisibleItemPosition > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, recyclerView)
            loading = true
        }
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0

        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }
}