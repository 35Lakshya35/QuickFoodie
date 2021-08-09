package com.example.quickfoodie.database.RestaurantDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntity::class],version = 1)
abstract class RestaurantDatabase:RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
}