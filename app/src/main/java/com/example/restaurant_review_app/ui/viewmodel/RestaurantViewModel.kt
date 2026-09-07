package com.example.restaurant_review_app.ui.viewmodel

import androidx.lifecycle.*
import com.example.restaurant_review_app.data.RestaurantRepository
import com.example.restaurant_review_app.data.model.Restaurant
import com.example.restaurant_review_app.data.model.Review
import com.example.restaurant_review_app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class FilterOptions(
    val minRating: Double = 0.0,
    val cuisine: String = "All",
    val maxPrice: String = "Any"
)

class RestaurantViewModel(private val repository: RestaurantRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    
    private val _filterOptions = MutableStateFlow(FilterOptions())
    val filterOptions: StateFlow<FilterOptions> = _filterOptions

    val allRestaurants: LiveData<List<Restaurant>> = combine(
        repository.allRestaurants,
        _searchQuery,
        _filterOptions
    ) { restaurants, query, filters ->
        restaurants.filter { restaurant ->
            val matchesSearch = query.isBlank() || 
                restaurant.name.contains(query, ignoreCase = true) || 
                restaurant.cuisine.contains(query, ignoreCase = true) ||
                restaurant.priceRange.contains(query)
            
            val matchesRating = restaurant.rating >= filters.minRating
            val matchesCuisine = filters.cuisine == "All" || restaurant.cuisine.contains(filters.cuisine, ignoreCase = true)
            val matchesPrice = filters.maxPrice == "Any" || restaurant.priceRange.length <= filters.maxPrice.length

            matchesSearch && matchesRating && matchesCuisine && matchesPrice
        }
    }.asLiveData()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilters(filters: FilterOptions) {
        _filterOptions.value = filters
    }

    fun getReviewsForRestaurant(restaurantId: Long): LiveData<List<Review>> {
        return repository.getReviewsForRestaurant(restaurantId).asLiveData()
    }

    suspend fun getRestaurantById(id: Long): Restaurant? {
        return repository.getRestaurantById(id)
    }

    fun addReview(review: Review) = viewModelScope.launch {
        repository.insertReview(review)
    }

    fun getReviewsForUser(userId: Long): LiveData<List<Review>> {
        return repository.getReviewsForUser(userId).asLiveData()
    }
    
    suspend fun getUserByEmail(email: String): User? {
        return repository.getUserByEmail(email)
    }
    
    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun getFavoriteRestaurants(userId: String): LiveData<List<Restaurant>> {
        return repository.getFavoriteRestaurants(userId).asLiveData()
    }

    fun toggleFavorite(userId: String, restaurantId: Long) = viewModelScope.launch {
        repository.toggleFavorite(userId, restaurantId)
    }

    suspend fun isFavorite(userId: String, restaurantId: Long): Boolean {
        return repository.isFavorite(userId, restaurantId)
    }
}

class RestaurantViewModelFactory(private val repository: RestaurantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
