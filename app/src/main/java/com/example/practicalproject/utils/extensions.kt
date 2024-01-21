package com.example.practicalproject.utils

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.showToast(string: String) =
    Toast.makeText(this, string, Toast.LENGTH_LONG).show()