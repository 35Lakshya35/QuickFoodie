package com.example.quickfoodie.database.RestaurantDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RestaurantsTable")
data class RestaurantEntity(
    @PrimaryKey var res_id:String,
    @ColumnInfo(name = "res_name") var resName:String,
    @ColumnInfo(name = "res_price") var resPrice:String,
    @ColumnInfo(name = "res_rating") var resRating:String,
    @ColumnInfo(name = "res_image") var resImage:String
)
