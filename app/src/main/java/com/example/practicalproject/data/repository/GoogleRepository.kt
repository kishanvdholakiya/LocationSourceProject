package com.example.practicalproject.data.repository

import com.example.practicalproject.data.model.GetDurationFromLatLngResponse
import retrofit2.http.QueryMap

interface GoogleRepository {
    suspend fun getDistanceFromLatLng(@QueryMap parameters: Map<String, String>): GetDurationFromLatLngResponse
}