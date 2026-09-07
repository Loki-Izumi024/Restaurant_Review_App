package com.example.restaurant_review_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant_review_app.data.AppDatabase
import com.example.restaurant_review_app.data.RestaurantRepository
import com.example.restaurant_review_app.data.model.User
import com.example.restaurant_review_app.databinding.ActivitySignupBinding
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModel
import com.example.restaurant_review_app.ui.viewmodel.RestaurantViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    private val viewModel: RestaurantViewModel by viewModels {
        val database = AppDatabase.getDatabase(this)
        RestaurantViewModelFactory(RestaurantRepository(database.restaurantDao(), database.reviewDao(), database.userDao(), database.favoriteDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val username = binding.signupUsername.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirmPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = User(username = username, email = email)
                        viewModel.insertUser(user)
                        
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.loginLink.setOnClickListener {
            finish()
        }
    }
}
