package com.example.practicalproject.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationSourceDao {
    @Query("SELECT * FROM locationsource")
    suspend fun getAllLocationSource(): MutableList<LocationSource>

    @Query("SELECT * FROM locationsource ORDER BY distance ASC")
    suspend fun getAllLocationSourceAscending(): MutableList<LocationSource>

    @Query("SELECT * FROM locationsource ORDER BY distance DESC")
    suspend fun getAllLocationSourceDescending(): MutableList<LocationSource>

    @Insert
    suspend fun insert(locationSource: LocationSource)

    @Query("DELETE FROM locationsource WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun delete(latitude: Double?, longitude: Double?)

    @Update
    suspend fun update(locationSource: LocationSource)

    @Query("SELECT * FROM locationsource WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun checkLocationSourceIfExists(latitude: Double?, longitude: Double?): MutableList<LocationSource>
}