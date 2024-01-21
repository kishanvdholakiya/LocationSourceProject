package com.example.practicalproject.data

import com.example.practicalproject.data.repository.GoogleRepository
import com.example.practicalproject.data.repository.GoogleRepositoryImpl
import com.example.practicalproject.data.service.GoogleService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DefaultAppContainer: AppContainer {
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URLFactory.GOOGLE_DIRECTIONS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override val googleRepository: GoogleRepository by lazy {
        GoogleRepositoryImpl(getRetrofit().create(GoogleService::class.java))
    }
}