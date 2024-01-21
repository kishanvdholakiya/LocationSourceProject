package com.example.practicalproject.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocationSource::class], version = 1)
abstract class LocationSourceDatabase : RoomDatabase() {
    abstract fun locationSourceDao(): LocationSourceDao
}