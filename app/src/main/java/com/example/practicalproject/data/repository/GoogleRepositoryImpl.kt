package com.example.practicalproject.data.repository

import com.example.practicalproject.data.model.GetDurationFromLatLngResponse
import com.example.practicalproject.data.service.GoogleService

class GoogleRepositoryImpl(private val googleService: GoogleService): GoogleRepository {
    override suspend fun getDistanceFromLatLng(parameters: Map<String, String>): GetDurationFromLatLngResponse =
        googleService.getDistanceFromLatLng(parameters)
}