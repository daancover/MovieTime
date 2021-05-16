package com.coverlabs.movietime.extension

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

private const val OFFSCREEN_PAGE_LIMIT = 3
private const val MIN_SIZE_PERCENT = 0.85f
private const val SIZE_CHANGE_RATE = 0.15f
private const val PAGE_MARGINS = 16

@SuppressLint("WrongConstant")
fun ViewPager2.setupCarousel() {
    offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
    val pageTransformer = CompositePageTransformer()
    pageTransformer.addTransformer(MarginPageTransformer(PAGE_MARGINS.dp()))
    pageTransformer.addTransformer { page, position ->
        val r = 1 - abs(position)
        page.scaleY = MIN_SIZE_PERCENT + r * SIZE_CHANGE_RATE
    }

    setPageTransformer(pageTransformer)
}

fun ViewPager2.setupSideTouchEvent() {
    var x1 = 0f
    var x2: Float
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> x1 = event.x
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                val deltaX: Float = x2 - x1
                if (abs(deltaX) > 20) {
                    if (x2 > x1) {
                        swipeRight()
                    } else {
                        swipeLeft()
                    }
                } else {
                    performClick()
                }
            }
        }

        true
    }
}

fun ViewPager2.swipeRight() {
    if (currentItem > 0) {
        currentItem -= 1
    }
}

fun ViewPager2.swipeLeft() {
    adapter?.itemCount?.let {
        if (currentItem < it - 1) {
            currentItem += 1
        }
    }
}