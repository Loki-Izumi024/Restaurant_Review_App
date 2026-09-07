package com.example.restaurant_review_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.restaurant_review_app.R
import com.example.restaurant_review_app.data.AppDatabase
import com.example.restaurant_review_app.data.RestaurantRepository
import com.example.restaurant_review_app.databinding.FragmentRestaurantDetailBinding
import com.example.restaurant_review_app.ui.adapter.ReviewAdapter
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModel
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RestaurantDetailFragment : Fragment() {

    private var _binding: FragmentRestaurantDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        RestaurantViewModelFactory(RestaurantRepository(database.restaurantDao(), database.reviewDao(), database.userDao(), database.favoriteDao()))
    }

    private var restaurantId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restaurantId = arguments?.getLong("restaurantId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReviewAdapter()
        binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.reviewsRecyclerView.adapter = adapter

        // Setup toolbar back button
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        lifecycleScope.launch {
            viewModel.getRestaurantById(restaurantId)?.let { restaurant ->
                binding.detailRestaurantName.text = restaurant.name
                binding.detailRestaurantCuisine.text = restaurant.cuisine
                binding.detailRestaurantLocation.text = restaurant.location
                binding.detailRestaurantRating.text = String.format("%.1f", restaurant.rating)
                binding.detailMenuHighlights.text = restaurant.menuHighlights

                Glide.with(this@RestaurantDetailFragment)
                    .load(restaurant.imageUrl)
                    .centerCrop()
                    .into(binding.detailRestaurantImage)

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                binding.favoriteToggle.isChecked = viewModel.isFavorite(userId, restaurantId)
                
                binding.favoriteToggle.setOnCheckedChangeListener { _, _ ->
                    viewModel.toggleFavorite(userId, restaurantId)
                }
            }
        }

        viewModel.getReviewsForRestaurant(restaurantId).observe(viewLifecycleOwner) { reviews ->
            adapter.submitList(reviews)
        }

        binding.addReviewFab.setOnClickListener {
            val bundle = Bundle().apply { putLong("restaurantId", restaurantId) }
            findNavController().navigate(R.id.action_restaurantDetailFragment_to_addReviewFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
