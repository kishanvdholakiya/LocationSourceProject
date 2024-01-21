package com.example.practicalproject

import android.app.Application
import com.example.practicalproject.data.AppContainer
import com.example.practicalproject.data.Credentials
import com.example.practicalproject.data.DefaultAppContainer
import com.example.practicalproject.room.LocationSourceDataBaseBuilder
import com.example.practicalproject.room.LocationSourceDataBaseHelper
import com.example.practicalproject.room.LocationSourceDataBaseHelperImpl
import com.google.android.libraries.places.api.Places

class LocationSourceApplication: Application() {

    val database by lazy { LocationSourceDataBaseBuilder.getInstance(this) }
    val locationSourceDataBaseHelperImpl by lazy { LocationSourceDataBaseHelperImpl(database) }

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        //Places SDK Initialisation
        Places.initialize(this, Credentials.API_KEY)
        container = DefaultAppContainer()
    }
}