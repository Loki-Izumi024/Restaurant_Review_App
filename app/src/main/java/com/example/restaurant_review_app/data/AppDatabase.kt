package com.example.restaurant_review_app.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.restaurant_review_app.data.dao.FavoriteDao
import com.example.restaurant_review_app.data.dao.RestaurantDao
import com.example.restaurant_review_app.data.dao.ReviewDao
import com.example.restaurant_review_app.data.dao.UserDao
import com.example.restaurant_review_app.data.model.FavoriteRestaurant
import com.example.restaurant_review_app.data.model.Restaurant
import com.example.restaurant_review_app.data.model.Review
import com.example.restaurant_review_app.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Database(entities = [Restaurant::class, Review::class, User::class, FavoriteRestaurant::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "restaurant_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Seed data if empty
                        INSTANCE?.let { database ->
                            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                                val restaurantDao = database.restaurantDao()
                                val count = restaurantDao.getAllRestaurants().first().size
                                if (count == 0) {
                                    seedDatabase(restaurantDao)
                                }
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDatabase(restaurantDao: RestaurantDao) {
            Log.d("AppDatabase", "Seeding database with initial restaurants")
            val restaurants = listOf(
                Restaurant(name = "Folk Heritage Museum Restaurant", location = "Thimphu", rating = 4.5, priceRange = "$$", cuisine = "Authentic Bhutanese", imageUrl = "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=500", menuHighlights = "Ema Datshi, Kewa Datshi, Red Rice"),
                Restaurant(name = "Babesa Village Restaurant", location = "Thimphu", rating = 4.7, priceRange = "$$", cuisine = "Traditional Bhutanese", imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=500", menuHighlights = "Shakam Paa, Sikam Paa, Butter Tea"),
                Restaurant(name = "Sonam Trophel Restaurant", location = "Paro", rating = 4.2, priceRange = "$", cuisine = "Bhutanese & Tibetan", imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500", menuHighlights = "Momos, Thukpa, Jasha Maroo"),
                Restaurant(name = "Bukhari at COMO Uma Paro", location = "Paro", rating = 4.9, priceRange = "$$$", cuisine = "Fine Dining Bhutanese", imageUrl = "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=500", menuHighlights = "Yak Meat Carpaccio, Saffron Risotto, Artisanal Local Cheeses"),
                Restaurant(name = "Galingkha Restaurant", location = "Thimphu", rating = 4.0, priceRange = "$$", cuisine = "Bhutanese & Continental", imageUrl = "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=500", menuHighlights = "Phaksha Paa, Cheese Steak, Buckwheat Pancakes"),
                Restaurant(name = "Ambient Cafe", location = "Thimphu", rating = 4.4, priceRange = "$$", cuisine = "Coffee & Desserts", imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=500", menuHighlights = "Chocolate Cake, Espresso, Grilled Sandwiches"),
                Restaurant(name = "Champaca Cafe", location = "Paro", rating = 4.6, priceRange = "$$", cuisine = "Coffee & Pastries", imageUrl = "https://images.unsplash.com/photo-1559925393-8be0ec41b50d?w=500", menuHighlights = "Paro Momos, Caramel Macchiato, Fruit Tart"),
                Restaurant(name = "The Bhutanese", location = "Thimphu", rating = 4.3, priceRange = "$$", cuisine = "Bhutanese Cuisine", imageUrl = "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?w=500", menuHighlights = "Ema Datshi, Red Rice, Suja"),
                Restaurant(name = "San Maru", location = "Thimphu", rating = 4.5, priceRange = "$$", cuisine = "Korean Cuisine", imageUrl = "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500", menuHighlights = "Bibimbap, Bulgogi, Kimchi"),
                Restaurant(name = "Mountain Cafe", location = "Paro", rating = 4.1, priceRange = "$$", cuisine = "International", imageUrl = "https://images.unsplash.com/photo-1493770348161-369560ae357d?w=500", menuHighlights = "Pizza, Burgers, Hot Chocolate"),
                Restaurant(name = "Chimi Lhakhang Village Restaurant", location = "Punakha", rating = 4.3, priceRange = "$$", cuisine = "Local Bhutanese", imageUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500", menuHighlights = "Ema Datshi, Red Rice, Fresh Vegetables"),
                Restaurant(name = "Swiss Guest House", location = "Bumthang", rating = 4.6, priceRange = "$$$", cuisine = "Bhutanese & Swiss", imageUrl = "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=500", menuHighlights = "Bumthang Cheese, Swiss Fondue, Buckwheat Pancakes"),
                Restaurant(name = "Cloud 9", location = "Thimphu", rating = 4.5, priceRange = "$$$", cuisine = "Burgers & Shakes", imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500", menuHighlights = "Gourmet Burgers, Hand-cut Fries, Milkshakes"),
                Restaurant(name = "Zombala 2", location = "Thimphu", rating = 4.2, priceRange = "$", cuisine = "Bhutanese & Chinese", imageUrl = "https://images.unsplash.com/photo-1552611052-33e04de081de?w=500", menuHighlights = "Beef Momos, Chili Chicken, Fried Rice"),
                Restaurant(name = "Brioche Cafe", location = "Thimphu", rating = 4.7, priceRange = "$$", cuisine = "Bakery & Cafe", imageUrl = "https://images.unsplash.com/photo-1495147466023-ac5c588e2e94?w=500", menuHighlights = "Croissants, Eclairs, Artisan Bread")
            )
            restaurants.forEach { restaurantDao.insertRestaurant(it) }
        }
    }
}
