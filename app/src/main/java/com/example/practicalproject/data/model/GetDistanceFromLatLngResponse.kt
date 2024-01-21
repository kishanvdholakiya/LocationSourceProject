package com.example.practicalproject.data.model

import com.google.gson.annotations.SerializedName

data class GetDurationFromLatLngResponse(
    @SerializedName("geocoded_waypoints")
    var geocodedWaypointsEntity: List<GeocodedWaypointsEntity> = listOf(),
    @SerializedName("routes")
    var routesEntity: List<RoutesEntity> = listOf(),
    @SerializedName("status")
    val status: String? = null
)

data class RoutesEntity(
    @SerializedName("bounds")
    var bounds: BoundsEntity? = null,
    @SerializedName("copyrights")
    val copyrights: String? = null,
    @SerializedName("legs")
    var legsEntity: List<LegsEntity> = listOf(),
    @SerializedName("overview_polyline")
    var overviewPolyline: OverviewPolylineEntity? = null,
    @SerializedName("summary")
    val summary: String? = null,
    @SerializedName("warnings")
    var warnings: List<String> = listOf(),
    @SerializedName("waypoint_order")
    var waypointOrder: List<String> = listOf()
)

data class OverviewPolylineEntity(
    @SerializedName("points")
    val points: String? = null
)

data class LegsEntity(
    @SerializedName("distance")
    var distance: DistanceEntity? = null,
    @SerializedName("duration")
    var duration: DurationEntity? = null,
    @SerializedName("end_address")
    val endAddress: String? = null,
    @SerializedName("end_location")
    var endLocation: EndLocationEntity? = null,
    @SerializedName("start_address")
    val startAddress: String? = null,
    @SerializedName("start_location")
    var startLocation: StartLocationEntity? = null,
    @SerializedName("steps")
    var stepsEntity: List<StepsEntity> = listOf(),
    @SerializedName("traffic_speed_entry")
    var trafficSpeedEntry: List<String> = listOf(),
    @SerializedName("via_waypoint")
    var viaWaypoint: List<String> = listOf()
)

data class StepsEntity(
    @SerializedName("distance")
    var distance: DistanceEntity? = null,
    @SerializedName("duration")
    var duration: DurationEntity? = null,
    @SerializedName("end_location")
    var endLocation: EndLocationEntity? = null,
    @SerializedName("html_instructions")
    val htmlInstructions: String? = null,
    @SerializedName("maneuver")
    val maneuver: String? = null,
    @SerializedName("polyline")
    var polyline: PolylineEntity? = null,
    @SerializedName("start_location")
    var startLocation: StartLocationEntity? = null,
    @SerializedName("travel_mode")
    val travelMode: String? = null
)

data class PolylineEntity(
    @SerializedName("points")
    val points: String? = null
)

data class EndLocationEntity(
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null
)

data class DurationEntity(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("value")
    var value: Int? = null
)

data class StartLocationEntity(
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null
)

data class DistanceEntity(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("value")
    var value: Int? = null
)

data class BoundsEntity(
    @SerializedName("northeast")
    var northeast: NortheastEntity? = null,
    @SerializedName("southwest")
    var southwest: SouthwestEntity? = null
)

data class SouthwestEntity(
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null
)

data class NortheastEntity(
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null
)

data class GeocodedWaypointsEntity(
    @SerializedName("geocoder_status")
    val geocoderStatus: String? = null,
    @SerializedName("place_id")
    val placeId: String? = null,
    @SerializedName("types")
    var types: List<String> = listOf()
)
