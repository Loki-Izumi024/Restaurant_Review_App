package com.example.restaurant_review_app.data.dao

import androidx.room.*
import com.example.restaurant_review_app.data.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE restaurantId = :restaurantId")
    fun getReviewsForRestaurant(restaurantId: Long): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE userId = :userId")
    fun getReviewsForUser(userId: Long): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Delete
    suspend fun deleteReview(review: Review)
}
