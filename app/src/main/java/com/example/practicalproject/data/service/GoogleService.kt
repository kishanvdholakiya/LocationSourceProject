package com.example.practicalproject.data.service

import com.example.practicalproject.data.URLFactory
import com.example.practicalproject.data.model.GetDurationFromLatLngResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface GoogleService {
    @GET(URLFactory.GOOGLE_DIRECTIONS)
    suspend fun getDistanceFromLatLng(@QueryMap parameters: Map<String, String>): GetDurationFromLatLngResponse
}