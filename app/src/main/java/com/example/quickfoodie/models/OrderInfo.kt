package com.example.quickfoodie.models

import com.example.quickfoodie.database.menuItemDB.MenuEntity

data class OrderInfo(
    var resName:String,
    val orderDate:String,
    var arrayList: ArrayList<MenuEntity>
)