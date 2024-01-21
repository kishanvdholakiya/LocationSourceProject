package com.example.practicalproject.dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.practicalproject.R
import com.example.practicalproject.databinding.SortByBottomSheetBinding
import com.example.practicalproject.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortByBottomSheet(
    private val isAscending: (Boolean) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: SortByBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.WhiteCustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SortByBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.imageViewClose.setOnClickListener {
            dismiss()
        }
        binding.buttonClear.setOnClickListener {
            binding.radioGroup.clearCheck()
        }
        binding.buttonApply.setOnClickListener {
            if (binding.radioGroup.checkedRadioButtonId != RadioGroup.NO_ID) {
                dismiss()
                isAscending(binding.radioAscending.isChecked)
            } else {
                requireContext().showToast(getString(R.string.msg_please_select_sort_option))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}