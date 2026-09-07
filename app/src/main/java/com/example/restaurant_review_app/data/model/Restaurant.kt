package com.example.restaurant_review_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val location: String,
    val rating: Double,
    val priceRange: String,
    val cuisine: String,
    val imageUrl: String,
    val menuHighlights: String = ""
)
