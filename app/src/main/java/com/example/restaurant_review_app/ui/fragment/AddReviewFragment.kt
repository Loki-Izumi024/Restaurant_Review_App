package com.example.restaurant_review_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.restaurant_review_app.data.AppDatabase
import com.example.restaurant_review_app.data.RestaurantRepository
import com.example.restaurant_review_app.data.model.Review
import com.example.restaurant_review_app.databinding.FragmentAddReviewBinding
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModel
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AddReviewFragment : Fragment() {

    private var _binding: FragmentAddReviewBinding? = null
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
        _binding = FragmentAddReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.submitReviewButton.setOnClickListener {
            val rating = binding.addReviewRatingBar.rating.toInt()
            val comment = binding.addReviewEditText.text.toString()

            if (comment.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                lifecycleScope.launch {
                    val user = viewModel.getUserByEmail(currentUser.email!!)
                    val userId: Long
                    val userName: String

                    if (user == null) {
                        // Create a local user if they don't exist yet
                        val newUser = com.example.restaurant_review_app.data.model.User(
                            username = currentUser.displayName ?: currentUser.email?.split("@")?.get(0) ?: "User",
                            email = currentUser.email!!
                        )
                        viewModel.insertUser(newUser)
                        val createdUser = viewModel.getUserByEmail(currentUser.email!!)
                        userId = createdUser?.id ?: 1L
                        userName = createdUser?.username ?: "User"
                    } else {
                        userId = user.id
                        userName = user.username
                    }
                    
                    val review = Review(
                        restaurantId = restaurantId,
                        userId = userId,
                        userName = userName,
                        rating = rating,
                        comment = comment
                    )
                    viewModel.addReview(review)
                    Toast.makeText(requireContext(), "Review submitted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            } else {
                Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
