package com.example.practicalproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practicalproject.R
import com.example.practicalproject.databinding.RowLocationSourceBinding
import com.example.practicalproject.room.LocationSource
import com.example.practicalproject.utils.gone
import com.example.practicalproject.utils.show

class LocationSourceAdapter(
    private val onEdit: (LocationSource) -> Unit,
    private val onDelete: (LocationSource) -> Unit
) : RecyclerView.Adapter<LocationSourceAdapter.ViewHolder>() {

    var data = mutableListOf<LocationSource>()
        set(value) {
            val positionStart = data.size + 1
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    inner class ViewHolder(
        private val binding: RowLocationSourceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(locationSource: LocationSource) {
            with(locationSource) {
                with(binding) {
                    textViewCityName.text = city
                    textViewAddress.text = address
                    textViewDistance.text =
                        itemView.context.getString(R.string.text_distance).format(distance)
                    if (data.minByOrNull { it.id } == locationSource) {
                        textViewPrimary.show()
                        textViewDistance.gone()
                    } else {
                        textViewPrimary.gone()
                        textViewDistance.show()
                    }
                    imageViewDelete.setOnClickListener { onDelete(locationSource) }
                    imageViewEdit.setOnClickListener { onEdit(locationSource) }
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        RowLocationSourceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun removeItem(locationSource: LocationSource) {
        val index = data.indexOf(locationSource)
        data.remove(locationSource)
        notifyItemRemoved(index)
    }
}