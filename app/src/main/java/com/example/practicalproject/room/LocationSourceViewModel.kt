package com.example.practicalproject.room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.practicalproject.LocationSourceApplication
import com.example.practicalproject.data.model.GetDurationFromLatLngResponse
import com.example.practicalproject.data.repository.GoogleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationSourceViewModel(
    private val locationSourceDataBaseHelper: LocationSourceDataBaseHelper,
    private val googleRepository: GoogleRepository
) : ViewModel() {

    val getAllLocationSourceLiveData = MutableLiveData<MutableList<LocationSource>>()
    val checkLocationSourceIfExistsLiveData = MutableLiveData<MutableList<LocationSource>>()
    val getDistanceFromLatLngLiveData = MutableLiveData<GetDurationFromLatLngResponse>()

    fun getAll() {
        viewModelScope.launch {
            getAllLocationSourceLiveData.value = locationSourceDataBaseHelper.getAllLocationSource()
        }
    }

    fun getAllLocationSourceAscending() {
        viewModelScope.launch {
            getAllLocationSourceLiveData.value = locationSourceDataBaseHelper.getAllLocationSourceAscending()
        }
    }

    fun getAllLocationSourceDescending() {
        viewModelScope.launch {
            getAllLocationSourceLiveData.value = locationSourceDataBaseHelper.getAllLocationSourceDescending()
        }
    }

    fun insert(locationSource: LocationSource) {
        viewModelScope.launch {
            locationSourceDataBaseHelper.insert(locationSource)
        }
    }

    fun delete(latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            locationSourceDataBaseHelper.delete(latitude, longitude)
        }
    }

    fun update(locationSource: LocationSource) {
        viewModelScope.launch {
            locationSourceDataBaseHelper.update(locationSource)
        }
    }

    fun checkLocationSourceIfExists(latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            checkLocationSourceIfExistsLiveData.value = locationSourceDataBaseHelper.checkLocationSourceIfExists(latitude, longitude)
        }
    }


    fun getDurationFromLatLng(
        sourceLatitude: Double,
        sourceLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        key: String
    ) {
        val parameters = HashMap<String, String>()
        parameters["origin"] = "$sourceLatitude,$sourceLongitude"
        parameters["destination"] = "$destinationLatitude,$destinationLongitude"
        parameters["key"] = key

        viewModelScope.launch {
            try {
                val result = googleRepository.getDistanceFromLatLng(parameters)
                getDistanceFromLatLngLiveData.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as LocationSourceApplication)
                val locationSourceDataBaseHelperImpl = application.locationSourceDataBaseHelperImpl
                LocationSourceViewModel(locationSourceDataBaseHelper = locationSourceDataBaseHelperImpl, googleRepository = application.container.googleRepository)
            }
        }
    }
}