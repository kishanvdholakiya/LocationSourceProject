package com.example.practicalproject.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicalproject.R
import com.example.practicalproject.activity.MainActivity
import com.example.practicalproject.adapter.LocationSourceAdapter
import com.example.practicalproject.databinding.DialogDeletePlaceBinding
import com.example.practicalproject.databinding.FragmentLocationSourceListBinding
import com.example.practicalproject.dialog.SortByBottomSheet
import com.example.practicalproject.room.LocationSource
import com.example.practicalproject.room.LocationSourceViewModel
import com.example.practicalproject.utils.FilterType
import com.example.practicalproject.utils.MarginItemDecoration
import com.example.practicalproject.utils.gone
import com.example.practicalproject.utils.show
import com.example.practicalproject.utils.showToast
import com.google.android.gms.maps.model.LatLng

class LocationSourceListFragment : Fragment() {
    private var _binding: FragmentLocationSourceListBinding? = null
    private val binding: FragmentLocationSourceListBinding get() = _binding!!

    private lateinit var selectedFilter: FilterType

    private val adapter by lazy {
        LocationSourceAdapter(
            onEdit = {
                editLocationSource(it)
            },
            onDelete = {
                deleteLocationSource(it)
            }
        )
    }

    private fun editLocationSource(locationSource: LocationSource) {
        openEditLocationFragment(locationSource)
    }

    private fun openEditLocationFragment(locationSource: LocationSource) {
        (activity as MainActivity).changeFragment(
            SearchPlacesFragment.newInstance(
                LatLng(locationSource.latitude ?: 0.0, locationSource.longitude ?: 0.0),
                locationSource.address,
                true
            ),
            true
        )
    }

    private fun deleteLocationSource(locationSource: LocationSource) {
        showAlertDialog(locationSource)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationSourceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val locationSourceViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            LocationSourceViewModel.Factory
        )[LocationSourceViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setupRecyclerView()
        setListeners()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LocationSourceListFragment.adapter
            addItemDecoration(MarginItemDecoration(8))
        }
    }

    private fun observeLiveData() {
        locationSourceViewModel.getAllLocationSourceLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.groupList.show()
                binding.groupNoLocations.gone()
                adapter.data = it
            } else {
                binding.groupList.gone()
                binding.groupNoLocations.show()
            }
        }

        fetchAllLocationSource()
    }

    private fun fetchAllLocationSource() {
        locationSourceViewModel.getAll()
    }

    private fun fetchAscendingLocationSource() {
        locationSourceViewModel.getAllLocationSourceAscending()
    }

    private fun fetchDescendingLocationSource() {
        locationSourceViewModel.getAllLocationSourceDescending()
    }

    private fun setListeners() {
        binding.buttonAddPoi.setOnClickListener {
            openAddPoiFragment()
        }
        binding.buttonAddPoiLarge.setOnClickListener {
            openAddPoiFragment()
        }
        binding.imageViewFilter.setOnClickListener {
            if (adapter.data.isNotEmpty()) {
                val modalBottomSheet = SortByBottomSheet { isAscending ->
                    if (isAscending) {
                        selectedFilter = FilterType.ASCENDING
                        fetchAscendingLocationSource()
                    } else {
                        selectedFilter = FilterType.DESCENDING
                        fetchDescendingLocationSource()
                    }
                }
                modalBottomSheet.show(
                    childFragmentManager,
                    SortByBottomSheet::class.java.simpleName
                )
            } else {
                requireContext().showToast(getString(R.string.msg_please_add_place))
            }
        }
        binding.imageViewDrawPath.setOnClickListener {
            if (::selectedFilter.isInitialized) {
                (activity as MainActivity).changeFragment(
                    RoutingFragment.newInstance(
                        selectedFilter == FilterType.ASCENDING,
                        selectedFilter == FilterType.DESCENDING
                    ),
                    true
                )
            } else {
                (activity as MainActivity).changeFragment(
                    RoutingFragment.newInstance(),
                    true
                )
            }
        }
    }

    private fun showAlertDialog(locationSource: LocationSource) {
        val binding = DialogDeletePlaceBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.buttonOk.setOnClickListener {
            locationSourceViewModel.delete(locationSource.latitude, locationSource.longitude)
            adapter.removeItem(locationSource)
            dialog.dismiss()
            if (adapter.data.isEmpty()) {
                this.binding.groupList.gone()
                this.binding.groupNoLocations.show()
            }
        }

        dialog.show()
    }

    private fun openAddPoiFragment() {
        if (adapter.data.isNotEmpty()) {
            val primaryLocation = adapter.data.minByOrNull { it.id }
            (activity as MainActivity).changeFragment(
                SearchPlacesFragment.newInstance(
                    LatLng(primaryLocation?.latitude ?: 0.0, primaryLocation?.longitude ?: 0.0)
                ),
                true
            )
        } else {
            (activity as MainActivity).changeFragment(
                SearchPlacesFragment.newInstance(),
                true
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}