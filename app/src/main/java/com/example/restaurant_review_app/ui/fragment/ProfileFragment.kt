package com.example.restaurant_review_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_review_app.LoginActivity
import com.example.restaurant_review_app.R
import com.example.restaurant_review_app.data.AppDatabase
import com.example.restaurant_review_app.data.RestaurantRepository
import com.example.restaurant_review_app.databinding.FragmentProfileBinding
import com.example.restaurant_review_app.ui.adapter.RestaurantAdapter
import com.example.restaurant_review_app.ui.adapter.ReviewAdapter
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModel
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        RestaurantViewModelFactory(RestaurantRepository(database.restaurantDao(), database.reviewDao(), database.userDao(), database.favoriteDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            return
        }

        binding.profileEmail.text = currentUser.email

        val restaurantAdapter = RestaurantAdapter { restaurant ->
            val bundle = Bundle().apply { putLong("restaurantId", restaurant.id) }
            findNavController().navigate(R.id.action_profileFragment_to_restaurantDetailFragment, bundle)
        }
        binding.savedRestaurantsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.savedRestaurantsRecyclerView.adapter = restaurantAdapter

        viewModel.getFavoriteRestaurants(currentUser.uid).observe(viewLifecycleOwner) { restaurants ->
            restaurantAdapter.submitList(restaurants)
        }

        lifecycleScope.launch {
            val user = viewModel.getUserByEmail(currentUser.email!!)
            if (user != null) {
                binding.profileUsername.text = user.username
                
                val adapter = ReviewAdapter()
                binding.userReviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.userReviewsRecyclerView.adapter = adapter
                
                viewModel.getReviewsForUser(user.id).observe(viewLifecycleOwner) { reviews ->
                    adapter.submitList(reviews)
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
