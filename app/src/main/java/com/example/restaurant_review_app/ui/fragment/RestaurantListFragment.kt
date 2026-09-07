package com.example.restaurant_review_app.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_review_app.R
import com.example.restaurant_review_app.data.AppDatabase
import com.example.restaurant_review_app.data.RestaurantRepository
import com.example.restaurant_review_app.databinding.FragmentRestaurantListBinding
import com.example.restaurant_review_app.ui.adapter.RestaurantAdapter
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModel
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModelFactory

class RestaurantListFragment : Fragment() {

    private var _binding: FragmentRestaurantListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        RestaurantViewModelFactory(RestaurantRepository(database.restaurantDao(), database.reviewDao(), database.userDao(), database.favoriteDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RestaurantAdapter { restaurant ->
            val bundle = Bundle().apply { putLong("restaurantId", restaurant.id) }
            findNavController().navigate(R.id.action_restaurantListFragment_to_restaurantDetailFragment, bundle)
        }

        binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.restaurantRecyclerView.adapter = adapter

        viewModel.allRestaurants.observe(viewLifecycleOwner) { restaurants ->
            adapter.submitList(restaurants)
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.filterButton.setOnClickListener {
            val filterSheet = FilterBottomSheetFragment(viewModel.filterOptions.value) { newFilters ->
                viewModel.updateFilters(newFilters)
            }
            filterSheet.show(childFragmentManager, "FilterBottomSheet")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
