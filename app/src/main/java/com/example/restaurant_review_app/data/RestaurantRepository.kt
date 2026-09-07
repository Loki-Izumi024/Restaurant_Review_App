package com.example.restaurant_review_app.data

import com.example.restaurant_review_app.data.dao.FavoriteDao
import com.example.restaurant_review_app.data.dao.RestaurantDao
import com.example.restaurant_review_app.data.dao.ReviewDao
import com.example.restaurant_review_app.data.dao.UserDao
import com.example.restaurant_review_app.data.model.FavoriteRestaurant
import com.example.restaurant_review_app.data.model.Restaurant
import com.example.restaurant_review_app.data.model.Review
import com.example.restaurant_review_app.data.model.User
import kotlinx.coroutines.flow.Flow

class RestaurantRepository(
    private val restaurantDao: RestaurantDao,
    private val reviewDao: ReviewDao,
    private val userDao: UserDao,
    private val favoriteDao: FavoriteDao
) {
    val allRestaurants: Flow<List<Restaurant>> = restaurantDao.getAllRestaurants()

    fun getReviewsForRestaurant(restaurantId: Long): Flow<List<Review>> {
        return reviewDao.getReviewsForRestaurant(restaurantId)
    }

    suspend fun insertRestaurant(restaurant: Restaurant) {
        restaurantDao.insertRestaurant(restaurant)
    }

    suspend fun insertReview(review: Review) {
        reviewDao.insertReview(review)
    }

    suspend fun getRestaurantById(id: Long): Restaurant? {
        return restaurantDao.getRestaurantById(id)
    }

    suspend fun getUserById(id: Long) = userDao.getUserById(id)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    fun getReviewsForUser(userId: Long) = reviewDao.getReviewsForUser(userId)

    fun getFavoriteRestaurants(userId: String): Flow<List<Restaurant>> {
        return favoriteDao.getFavoriteRestaurants(userId)
    }

    suspend fun toggleFavorite(userId: String, restaurantId: Long) {
        val favorite = favoriteDao.getFavorite(userId, restaurantId)
        if (favorite == null) {
            favoriteDao.insertFavorite(FavoriteRestaurant(userId = userId, restaurantId = restaurantId))
        } else {
            favoriteDao.deleteFavorite(userId, restaurantId)
        }
    }

    suspend fun isFavorite(userId: String, restaurantId: Long): Boolean {
        return favoriteDao.getFavorite(userId, restaurantId) != null
    }
}
