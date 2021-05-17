package com.coverlabs.movietime.extension

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

fun Activity.setSupportActionBar(toolbar: Toolbar) {
    (this as? AppCompatActivity)?.apply {
        setSupportActionBar(toolbar)
    }
}