package com.example.quickfoodie.database.menuItemDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MenuDao {

    @Insert
    fun insert(menuEntity: MenuEntity)

    @Delete
    fun delete(menuEntity: MenuEntity)

    @Delete
    fun deleteAll(menuList:List<MenuEntity>)

    @Query("SELECT * FROM MenuTable")
    fun getAllData():List<MenuEntity>

    @Query("SELECT * FROM MenuTable WHERE menu_id=:id")
    fun getDataById(id:String):MenuEntity
}