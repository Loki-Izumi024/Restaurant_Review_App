package com.example.restaurant_review_app.data.dao

import androidx.room.*
import com.example.restaurant_review_app.data.model.FavoriteRestaurant
import com.example.restaurant_review_app.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteRestaurant)

    @Query("DELETE FROM favorite_restaurants WHERE userId = :userId AND restaurantId = :restaurantId")
    suspend fun deleteFavorite(userId: String, restaurantId: Long)

    @Query("SELECT * FROM favorite_restaurants WHERE userId = :userId AND restaurantId = :restaurantId")
    suspend fun getFavorite(userId: String, restaurantId: Long): FavoriteRestaurant?

    @Query("""
        SELECT restaurants.* FROM restaurants 
        INNER JOIN favorite_restaurants ON restaurants.id = favorite_restaurants.restaurantId 
        WHERE favorite_restaurants.userId = :userId
    """)
    fun getFavoriteRestaurants(userId: String): Flow<List<Restaurant>>
}
