package com.example.quickfoodie.database.menuItemDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MenuTable")
data class MenuEntity(
    @PrimaryKey var menu_id: String,
    @ColumnInfo(name = "menu_name") var menuName: String?,
    @ColumnInfo(name = "menu_price") var menuPrice: String?
)