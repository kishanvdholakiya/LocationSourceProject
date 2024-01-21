package com.example.practicalproject.room

interface LocationSourceDataBaseHelper {
    suspend fun getAllLocationSource(): MutableList<LocationSource>

    suspend fun getAllLocationSourceAscending(): MutableList<LocationSource>

    suspend fun getAllLocationSourceDescending(): MutableList<LocationSource>

    suspend fun insert(locationSource: LocationSource)

    suspend fun delete(latitude: Double?, longitude: Double?)

    suspend fun update(locationSource: LocationSource)

    suspend fun checkLocationSourceIfExists(latitude: Double?, longitude: Double?): MutableList<LocationSource>
}