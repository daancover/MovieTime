package com.coverlabs.movietime.extension

import android.content.res.Resources
import android.util.TypedValue

fun Int.dp(resources: Resources): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    ).toInt()
}