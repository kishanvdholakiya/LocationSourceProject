package com.example.practicalproject.fragment

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicalproject.R
import com.example.practicalproject.adapter.MarkerInfoWindowAdapter
import com.example.practicalproject.adapter.PlaceAutocompleteRecyclerViewAdapter
import com.example.practicalproject.databinding.FragmentSearchPlacesBinding
import com.example.practicalproject.room.LocationSource
import com.example.practicalproject.room.LocationSourceViewModel
import com.example.practicalproject.utils.gone
import com.example.practicalproject.utils.show
import com.example.practicalproject.utils.showToast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.SphericalUtil
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Locale

class SearchPlacesFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentSearchPlacesBinding? = null
    private val binding: FragmentSearchPlacesBinding get() = _binding!!

    private lateinit var map: GoogleMap

    private lateinit var place: Place

    private val placesClient by lazy {
        Places.createClient(requireActivity().applicationContext)
    }

    private val locationLatitude by lazy { arguments?.getDouble(LOCATION_LATITUDE) }

    private val locationLongitude by lazy { arguments?.getDouble(LOCATION_LONGITUDE) }

    private val address by lazy { arguments?.getString(LOCATION_ADDRESS) }

    private val isEdit by lazy { arguments?.getBoolean(IS_EDIT) }

    private val locationSourceViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            LocationSourceViewModel.Factory
        )[LocationSourceViewModel::class.java]
    }

    private val placeAutocompleteRecyclerViewAdapter by lazy {
        PlaceAutocompleteRecyclerViewAdapter(requireActivity(), placesClient)
    }

    private fun setMap() {
        val supportMapFragment = SupportMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.mapFragment, supportMapFragment)
            .commit()
        supportMapFragment.getMapAsync(this)
    }

    private fun observeLiveData() {
        locationSourceViewModel.checkLocationSourceIfExistsLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                try {
                    val addresses = Geocoder(
                        requireContext(),
                        Locale.getDefault()
                    ).getFromLocation(
                        place.latLng?.latitude ?: 0.0,
                        place.latLng?.longitude ?: 0.0,
                        1
                    )

                    if (!addresses.isNullOrEmpty()) {

                        val locationSource = LocationSource(
                            latitude = place.latLng?.latitude,
                            longitude = place.latLng?.longitude,
                            city = addresses.first().locality,
                            address = place.address,
                            distance = if (locationLatitude != null && locationLongitude != null) distanceInKM(place.latLng) else 0.0,
                        )
                        if (isEdit == true) {
                            locationSourceViewModel.delete(locationLatitude, locationLongitude)
                        }
                        locationSourceViewModel.insert(locationSource)
                        parentFragmentManager.popBackStack()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                hideSavePopup()
                requireContext().showToast(getString(R.string.text_location_source_already_added_please_choose_different_location))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMap()
        observeLiveData()
        setupRecyclerView()
        setupEditText()
        setupClickListeners()
    }

    private fun setupSelectedLocationForEditLocation() {
        if (isEdit == true) {
            binding.buttonSave.text = getString(R.string.label_update)
            clearMapAndAddMarker(locationLatitude ?: 0.0, locationLongitude ?: 0.0, address)
        }
    }

    private fun calculateDistance(latLng: LatLng?): Double {
        val fromLocation =
            LatLng(locationLatitude ?: 0.0, locationLongitude ?: 0.0)
        val distance = SphericalUtil.computeDistanceBetween(fromLocation, latLng)
        return String.format("%.2f", distance / 1000).toDouble()
    }

    private fun distanceInKM(latLng: LatLng?): Double {
        val results = FloatArray(1)
        Location.distanceBetween(locationLatitude ?: 0.0,locationLongitude ?: 0.0,latLng?.latitude ?: 0.0,latLng?.longitude ?: 0.0,results)
        return String.format("%.2f", (results.first() / 1000)).toDouble()
    }

    private fun setupClickListeners() {
        binding.imageViewBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.buttonSave.setOnClickListener {
            if (::place.isInitialized) {
                locationSourceViewModel.checkLocationSourceIfExists(
                    place.latLng?.latitude,
                    place.latLng?.longitude
                )
            }
        }
    }

    private fun setupEditText() {
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hideSavePopup()
                if (!s.isNullOrBlank()) {
                    placeAutocompleteRecyclerViewAdapter.filter.filter(s.toString())
                    binding.groupList.show()
                } else {
                    binding.groupList.gone()
                    placeAutocompleteRecyclerViewAdapter.mResultList.clear()
                    placeAutocompleteRecyclerViewAdapter.notifyItemRangeRemoved(
                        0,
                        placeAutocompleteRecyclerViewAdapter.mResultList.size
                    )
                }
            }
        })
    }

    private fun hideSavePopup() {
        binding.linearSavePlace.gone()
    }

    private fun showSavePopup() {
        binding.linearSavePlace.show()
    }

    private fun setupRecyclerView() {
        placeAutocompleteRecyclerViewAdapter.setOnItemClickListener(
            object : PlaceAutocompleteRecyclerViewAdapter.OnItemClickListener {
                override fun showToast(s: String?) {

                }

                override fun onRecyclerItemClick(view: View?, position: Int) {
                    placeAutocompleteRecyclerViewAdapter.getPlaceByPlaceId(
                        placeAutocompleteRecyclerViewAdapter.mResultList[position].placeId,
                        object : PlaceAutocompleteRecyclerViewAdapter.PlaceSearchListener {
                            override fun onPlaceFound(place: Place?) {
                                place?.latLng?.latitude?.let { latitude ->
                                    place.latLng?.longitude?.let { longitude ->
                                        if (::map.isInitialized) {
                                            this@SearchPlacesFragment.place = place
                                            hideKeyboard()
                                            binding.editText.text?.clear()
                                            showSavePopup()
                                            clearMapAndAddMarker(latitude, longitude, place.address)
                                        }
                                    }
                                }
                            }

                            override fun onPlaceNotFound(message: String?) {
                                Log.e("Place", "Place not found")
                            }
                        }
                    )
                }
            }
        )
        binding.recyclerView.apply {
            layoutParams.height = resources.displayMetrics.heightPixels * 25 / 100
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placeAutocompleteRecyclerViewAdapter
        }
    }

    private fun clearMapAndAddMarker(latitude: Double, longitude: Double, address: String?) {
        map.clear()
        map.setInfoWindowAdapter(
            MarkerInfoWindowAdapter(
                requireContext()
            )
        )
        val latLng = LatLng(latitude, longitude)
        val marker =
            map.addMarker(MarkerOptions().position(latLng))
        marker?.title = address
        marker?.showInfoWindow()
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                15f
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        this.map = p0
        this.map.uiSettings.isZoomGesturesEnabled = true
        setupSelectedLocationForEditLocation()
    }

    fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }


    companion object {
        const val LOCATION_LATITUDE = "PRIMARY_LOCATION_LATITUDE"
        const val LOCATION_LONGITUDE = "PRIMARY_LOCATION_LONGITUDE"
        const val IS_EDIT = "IS_EDIT"
        const val IS_ASCENDING_ROUTING = "IS_ASCENDING_ROUTING"
        const val IS_DESCENDING_ROUTING = "IS_DESCENDING_ROUTING"
        const val LOCATION_ADDRESS = "LOCATION_ADDRESS"

        fun newInstance(
            latLng: LatLng? = null,
            address: String? = null,
            isEdit: Boolean = false,
            isAscendingRouting: Boolean = false,
            isDescendingRouting: Boolean = false
        ): SearchPlacesFragment {
            return SearchPlacesFragment().apply {
                latLng?.let {
                    arguments = bundleOf(
                        LOCATION_LATITUDE to latLng.latitude,
                        LOCATION_LONGITUDE to latLng.longitude,
                        IS_EDIT to isEdit,
                        IS_ASCENDING_ROUTING to isAscendingRouting,
                        IS_DESCENDING_ROUTING to isDescendingRouting,
                        LOCATION_ADDRESS to address
                    )
                }
            }
        }
    }
}


