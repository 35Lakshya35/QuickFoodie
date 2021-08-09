package com.example.quickfoodie.database.RestaurantDB

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class ResDBAsyncTask(var context: Context, var resEntity: RestaurantEntity?, var mode: Int) :
    AsyncTask<Void, Void, ArrayList<RestaurantEntity>>() {

    var db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "FoodData").build()

    /*Mode 1 :-add entity to database
      Mode 2 :-delete entity from database
      mode 3 :-get entity by id
      mode 4 :-get all entities as List
     */

    override fun doInBackground(vararg p0: Void?): ArrayList<RestaurantEntity> {

        when (mode) {
            1 -> {
                db.restaurantDao().insert(resEntity as RestaurantEntity)
                db.close()
            }
            2 -> {
                db.restaurantDao().delete(resEntity as RestaurantEntity)
                db.close()
            }
            3 -> {
                val entity = db.restaurantDao().getResById(resEntity?.res_id.toString())
                db.close()
                if(entity!=null)  //It is showing warning here but entity may be null
                    return arrayListOf<RestaurantEntity>(entity)  //List with only one entity

            }
            4 -> {
                val entityList = db.restaurantDao().getAllRestaurants()
                db.close()
                return entityList as ArrayList<RestaurantEntity>  //List with all entities
            }
        }
        return arrayListOf<RestaurantEntity>()
    }
}