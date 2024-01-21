package com.example.practicalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.example.practicalproject.R
import com.example.practicalproject.databinding.InfoWindowBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoWindowAdapter(
    private val context: Context
): GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker): View? = null

    override fun getInfoWindow(p0: Marker): View? {
        val title = p0.title ?: ""

        return if (title.isNotBlank()) {
            val view = LayoutInflater.from(context).inflate(R.layout.info_window, null)
            view.findViewById<AppCompatTextView>(R.id.tvTitle).text = title
            view
        } else {
            null
        }
    }
}