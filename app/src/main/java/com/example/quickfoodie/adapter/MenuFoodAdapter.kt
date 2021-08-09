package com.example.quickfoodie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.quickfoodie.R
import com.example.quickfoodie.database.menuItemDB.MenuDBAsyncTask
import com.example.quickfoodie.database.menuItemDB.MenuEntity
import com.example.quickfoodie.models.MenuInfo

class MenuFoodAdapter(
    var context: Context,
    var data: ArrayList<MenuEntity>,
    var btnProceedToCart: Button
) :
    RecyclerView.Adapter<MenuFoodAdapter.MenuViewHolder>() {

    init {
        val listAll=MenuDBAsyncTask(context,null,4,null).execute().get()
        if(listAll.isNotEmpty())
            btnProceedToCart.visibility=View.VISIBLE
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutMenu: LinearLayout = view.findViewById(R.id.singleRowMenu)
        val txtSrNo: TextView = view.findViewById(R.id.txtSrNo)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.single_row_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuEntity = data[position]
        holder.txtSrNo.text = (position + 1).toString()
        holder.txtItemName.text = menuEntity.menuName
        holder.txtItemPrice.text = "Rs.${menuEntity.menuPrice}"

        //Fetching entity from database
        val entityList=MenuDBAsyncTask(context,menuEntity,3,null).execute().get()

        //Setting text and color of button when view is created
        if(entityList.isNotEmpty()){
            //entity is present in database
            holder.btnAdd.text="Remove"
            holder.btnAdd.setBackgroundColor(context.resources.getColor(R.color.yellow))
        }
        else{
            holder.btnAdd.text="Add"
            holder.btnAdd.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))
        }


        //Add button click listener
        holder.btnAdd.setOnClickListener {
            //Setting button text,color and manage database
            val entityList1=MenuDBAsyncTask(context,menuEntity,3,null).execute().get()
            if(entityList1.isEmpty()){
                //entity is not in database(add it)
                MenuDBAsyncTask(context,menuEntity,1,null).execute().get()
                holder.btnAdd.text="Remove"
                holder.btnAdd.setBackgroundColor(context.resources.getColor(R.color.yellow))
            }
            else{
                //entity is in database(remove it)
                MenuDBAsyncTask(context,menuEntity,2,null).execute().get()
                holder.btnAdd.text="Add"
                holder.btnAdd.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))
            }

            //Setting visibility of proceed button
            val listAll1=MenuDBAsyncTask(context,null,4,null).execute().get()
            if(listAll1.isEmpty())
                btnProceedToCart.visibility=View.GONE
            else
                btnProceedToCart.visibility=View.VISIBLE
        }
    }
}