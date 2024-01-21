package com.example.practicalproject.room

class LocationSourceDataBaseHelperImpl(private val locationSourceDatabase: LocationSourceDatabase): LocationSourceDataBaseHelper {
    override suspend fun getAllLocationSource(): MutableList<LocationSource> =
        locationSourceDatabase.locationSourceDao().getAllLocationSource()

    override suspend fun getAllLocationSourceAscending(): MutableList<LocationSource> =
        locationSourceDatabase.locationSourceDao().getAllLocationSourceAscending()

    override suspend fun getAllLocationSourceDescending(): MutableList<LocationSource> =
        locationSourceDatabase.locationSourceDao().getAllLocationSourceDescending()

    override suspend fun insert(locationSource: LocationSource) =
        locationSourceDatabase.locationSourceDao().insert(locationSource)

    override suspend fun delete(latitude: Double?, longitude: Double?) =
        locationSourceDatabase.locationSourceDao().delete(latitude, longitude)

    override suspend fun update(locationSource: LocationSource) =
        locationSourceDatabase.locationSourceDao().update(locationSource)

    override suspend fun checkLocationSourceIfExists(latitude: Double?, longitude: Double?): MutableList<LocationSource> =
        locationSourceDatabase.locationSourceDao().checkLocationSourceIfExists(latitude, longitude)
}