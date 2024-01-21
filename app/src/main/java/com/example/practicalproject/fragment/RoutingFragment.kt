package com.example.practicalproject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.practicalproject.R
import com.example.practicalproject.data.Credentials
import com.example.practicalproject.databinding.FragmentRoutingBinding
import com.example.practicalproject.room.LocationSource
import com.example.practicalproject.room.LocationSourceViewModel
import com.example.practicalproject.utils.gone
import com.example.practicalproject.utils.show
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoutingFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentRoutingBinding? = null
    private val binding: FragmentRoutingBinding get() = _binding!!
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutingBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val isAscendingRouting by lazy { arguments?.getBoolean(IS_ASCENDING_ROUTING) }

    private val isDescendingRouting by lazy { arguments?.getBoolean(IS_DESCENDING_ROUTING) }

    private val locationSourceViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            LocationSourceViewModel.Factory
        )[LocationSourceViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMap()
        observeLiveData()
        setupClickListeners()
    }

    private fun setMap() {
        val supportMapFragment = SupportMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.mapFragment, supportMapFragment)
            .commit()
        supportMapFragment.getMapAsync(this)
    }

    private fun observeLiveData() {
        locationSourceViewModel.getAllLocationSourceLiveData.observe(viewLifecycleOwner) { locationSources ->
            drawDrivingPath(locationSources)
        }
    }

    private fun setupClickListeners() {
        binding.imageViewBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        this.map = p0
        this.map.uiSettings.setAllGesturesEnabled(true)
        /*drawDrivingPath(listOf(LatLng( 12.971599, 77.594566),
            LatLng(19.076065907851138, 72.87772145767256),
            LatLng(21.170240, 72.831062),
            LatLng(21.76463451446053, 72.15185708465485),
            LatLng(21.092159, 71.770462),
            LatLng(22.30404392904373, 70.80216781349114)))*/
        drawRouting()
    }

    private fun fetchAscendingLocationSource() {
        locationSourceViewModel.getAllLocationSourceAscending()
    }

    private fun fetchDescendingLocationSource() {
        locationSourceViewModel.getAllLocationSourceDescending()
    }

    private fun fetchAllLocationSource() {
        locationSourceViewModel.getAll()
    }

    private fun drawRouting() {
        showGenerateRouteProgressDialog()
        when {
            isAscendingRouting == true -> {
                fetchAscendingLocationSource()
            }
            isDescendingRouting == true -> {
                fetchDescendingLocationSource()
            }
            else -> {
                fetchAllLocationSource()
            }
        }
    }

    private fun showGenerateRouteProgressDialog() {
        binding.generateRouteProgressDialog.show()
    }

    private fun hideGenerateRouteProgressDialog() = binding.generateRouteProgressDialog.gone()

    private fun drawDrivingPath(ls: MutableList<LocationSource>) {
        if (::map.isInitialized) {
            val context = GeoApiContext.Builder()
                .apiKey(Credentials.API_KEY)
                .build()

            ls.forEach {
                map.addMarker(MarkerOptions().position(LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0)).title(it.address))
            }
            val coordinates = ls.map { LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0) }
            val options = PolylineOptions().width(10f).color(
                ContextCompat.getColor(requireContext(),
                    R.color.color_FF0066)).geodesic(true)

            lifecycleScope.launch(Dispatchers.IO) {
                for (i in 0 until coordinates.size - 1) {
                    val origin = coordinates[i]
                    val destination = coordinates[i + 1]

                    val result: DirectionsResult = DirectionsApi.newRequest(context)
                        .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                        .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                        .mode(TravelMode.DRIVING)
                        .await()

                    val points = result.routes[0].overviewPolyline.decodePath()

                    for (point in points) {
                        options.add(LatLng(point.lat, point.lng))
                    }
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    path = map.addPolyline(options)

                    // Move camera to the first coordinate
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates.first(), 15f))

                    hideGenerateRouteProgressDialog()
                }
            }
        }
    }
    private lateinit var path: Polyline

    companion object {
        const val IS_ASCENDING_ROUTING = "IS_ASCENDING_ROUTING"
        const val IS_DESCENDING_ROUTING = "IS_DESCENDING_ROUTING"

        fun newInstance(
            isAscendingRouting: Boolean = false,
            isDescendingRouting: Boolean = false
        ): RoutingFragment {
            return RoutingFragment().apply {
                arguments = bundleOf(
                    IS_ASCENDING_ROUTING to isAscendingRouting,
                    IS_DESCENDING_ROUTING to isDescendingRouting,
                )
            }
        }
    }
}