package com.example.practicalproject.data

import com.example.practicalproject.data.repository.GoogleRepository

interface AppContainer {
    val googleRepository: GoogleRepository
}