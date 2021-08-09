package com.example.quickfoodie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.quickfoodie.R
import com.example.quickfoodie.database.menuItemDB.MenuEntity

class CartAdapter(
    var context: Context,
    var data: ArrayList<MenuEntity>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFoodItem: TextView = view.findViewById(R.id.txtFoodItem)
        val txtFoodCost: TextView = view.findViewById(R.id.txtFoodCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.single_row_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val entity = data[position]
        holder.txtFoodItem.text = entity.menuName
        holder.txtFoodCost.text = "Rs." + entity.menuPrice
    }
}