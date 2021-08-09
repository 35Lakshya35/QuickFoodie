package com.example.quickfoodie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quickfoodie.R
import com.example.quickfoodie.activity.MenuCartActivity
import com.example.quickfoodie.database.RestaurantDB.ResDBAsyncTask
import com.example.quickfoodie.database.RestaurantDB.RestaurantEntity
import com.example.quickfoodie.models.RestaurantInfo
import com.squareup.picasso.Picasso

class RestaurantAdapter(var context: Context, var data: ArrayList<RestaurantInfo>) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>(), Filterable {

    var fixedData: ArrayList<RestaurantInfo> = arrayListOf()

    init {
        fixedData.addAll(data)
    }


    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val singleRowItem: LinearLayout = view.findViewById(R.id.singleRowItem)
        val imgFood: ImageView = view.findViewById(R.id.imgFood)
        val txtName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val imgHeart: ImageView = view.findViewById(R.id.imgHeart)
        val txtRating: TextView = view.findViewById(R.id.txtRating)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.single_row_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val resInfo = data[position]
        holder.txtName.text = resInfo.ResName
        holder.txtPrice.text = resInfo.ResPrice + "/person"
        holder.txtRating.text = resInfo.ResRating
        Picasso.get().load(resInfo.ResImage).error(R.drawable.default_food).into(holder.imgFood)

        val entity =
            RestaurantEntity(
                resInfo.ResId,
                resInfo.ResName,
                resInfo.ResPrice,
                resInfo.ResRating,
                resInfo.ResImage
            )

        //Fetching entity from database
        val resEntityList = ResDBAsyncTask(
            context,
            entity,
            3
        ).execute().get()
        if (resEntityList.isEmpty())
            holder.imgHeart.setImageResource(R.drawable.ic_fav_border)  //entity is not present in database
        else
            holder.imgHeart.setImageResource(R.drawable.ic_fav_filled)  //entity is present in database

        //Heart Button Click Listener
        holder.imgHeart.setOnClickListener {
            val resEntityList1 = ResDBAsyncTask(
                context,
                entity,
                3
            ).execute().get()

            //Entity is not present(Add it to Database)
            if (resEntityList1.isEmpty()) {
                ResDBAsyncTask(
                    context,
                    entity,
                    1
                ).execute().get()
                holder.imgHeart.setImageResource(R.drawable.ic_fav_filled)
            }
            //Entity is present(Remove it from database)
            else {
                ResDBAsyncTask(
                    context,
                    entity,
                    2
                ).execute().get()
                holder.imgHeart.setImageResource(R.drawable.ic_fav_border)
            }

        }

        holder.singleRowItem.setOnClickListener {
            val intent= Intent(context,MenuCartActivity::class.java)
            intent.putExtra("ResId",resInfo.ResId)
            intent.putExtra("ResName",resInfo.ResName)
            context.startActivity(intent)
            (context as AppCompatActivity).finish()
        }
    }




    /*
    Note:-
    value of (fixedData) will always remains same
    value of (data) will change according to filter
    */

    override fun getFilter(): Filter {
        data.removeAll(data)
        data.addAll(fixedData)
        return filterObj
    }

    var filterObj = object : Filter() {
        //Run automatically on background thread
        @SuppressLint("DefaultLocale")
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filterList = arrayListOf<RestaurantInfo>()
            if (p0.toString().isEmpty()) {
                filterList.addAll(data)
            } else {
                for (ResItem in data) {
                    if (ResItem.ResName.toLowerCase().contains(p0.toString().toLowerCase()))
                        filterList.add(ResItem)
                }
            }
            val filterResult = FilterResults()
            filterResult.values = filterList
            return filterResult
        }

        //Run on UI thread
        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            data.removeAll(data)
            data.addAll(p1?.values as ArrayList<RestaurantInfo>)
            notifyDataSetChanged()
        }

    }
}