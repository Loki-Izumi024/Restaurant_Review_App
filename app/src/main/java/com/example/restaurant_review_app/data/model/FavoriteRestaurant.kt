package com.example.restaurant_review_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_restaurants")
data class FavoriteRestaurant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val restaurantId: Long
)
