package com.example.quickfoodie.database.RestaurantDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insert(restaurantEntity: RestaurantEntity)

    @Delete
    fun delete(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM RestaurantsTable WHERE res_id=:id")
    fun getResById(id:String): RestaurantEntity

    @Query("SELECT * FROM RestaurantsTable")
    fun getAllRestaurants():List<RestaurantEntity>
}