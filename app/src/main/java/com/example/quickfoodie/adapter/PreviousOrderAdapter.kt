package com.example.quickfoodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickfoodie.R
import com.example.quickfoodie.models.OrderInfo

class PreviousOrderAdapter(var context: Context, var data: ArrayList<OrderInfo>) :
    RecyclerView.Adapter<PreviousOrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantNameOrder:TextView=view.findViewById(R.id.txtRestaurantNameOrder)
        val txtOrderDate:TextView=view.findViewById(R.id.txtOrderDate)
        val recyclerViewOrderChild:RecyclerView=view.findViewById(R.id.recyclerViewOrderChild)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.single_row_order,parent,false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderInfo=data[position]

        holder.txtOrderDate.text=orderInfo.orderDate
        holder.txtRestaurantNameOrder.text=orderInfo.resName

        //Setting child recycler view
        val layoutManagerOrder=LinearLayoutManager(context)
        val adapterOrder=CartAdapter(context,orderInfo.arrayList)
        holder.recyclerViewOrderChild.adapter=adapterOrder
        holder.recyclerViewOrderChild.layoutManager=layoutManagerOrder
    }
}