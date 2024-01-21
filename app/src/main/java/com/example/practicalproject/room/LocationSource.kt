package com.example.practicalproject.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationSource(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "distance") val distance: Double?
)
