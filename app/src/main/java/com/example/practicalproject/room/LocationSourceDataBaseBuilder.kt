package com.example.practicalproject.room

import android.content.Context
import androidx.room.Room

object LocationSourceDataBaseBuilder {
    private const val DATABASE_NAME = "location-source-database"

    private var INSTANCE: LocationSourceDatabase? = null

    fun getInstance(context: Context): LocationSourceDatabase {
        if (INSTANCE == null) {
            synchronized(LocationSourceDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) = Room.databaseBuilder(
        context = context,
        LocationSourceDatabase::class.java,
        DATABASE_NAME
    ).build()
}