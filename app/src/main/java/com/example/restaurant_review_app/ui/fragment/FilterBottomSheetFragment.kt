package com.example.restaurant_review_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.restaurant_review_app.R
import com.example.restaurant_review_app.databinding.LayoutFilterBottomSheetBinding
import com.example.restaurant_review_app.ui.viewmodel.FilterOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheetFragment(
    private val currentFilters: FilterOptions,
    private val onFiltersApplied: (FilterOptions) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: LayoutFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup UI with current filters
        binding.ratingSlider.value = currentFilters.minRating.toFloat()

        val cuisines = listOf("All", "Authentic Bhutanese", "Traditional Bhutanese", "Bhutanese & Tibetan", "Fine Dining Bhutanese", "Bhutanese & Continental")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cuisines)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.cuisineSpinner.adapter = adapter
        binding.cuisineSpinner.setSelection(cuisines.indexOf(currentFilters.cuisine))

        when (currentFilters.maxPrice) {
            "$" -> binding.chipLowPrice.isChecked = true
            "$$" -> binding.chipMedPrice.isChecked = true
            "$$$" -> binding.chipHighPrice.isChecked = true
            else -> binding.chipAnyPrice.isChecked = true
        }

        binding.applyFiltersButton.setOnClickListener {
            val selectedPrice = when (binding.priceChipGroup.checkedChipId) {
                R.id.chipLowPrice -> "$"
                R.id.chipMedPrice -> "$$"
                R.id.chipHighPrice -> "$$$"
                else -> "Any"
            }

            val newFilters = FilterOptions(
                minRating = binding.ratingSlider.value.toDouble(),
                cuisine = binding.cuisineSpinner.selectedItem.toString(),
                maxPrice = selectedPrice
            )
            onFiltersApplied(newFilters)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
