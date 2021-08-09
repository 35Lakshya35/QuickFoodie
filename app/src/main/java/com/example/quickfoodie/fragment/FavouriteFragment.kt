package com.example.quickfoodie.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickfoodie.R
import com.example.quickfoodie.adapter.RestaurantAdapter
import com.example.quickfoodie.database.RestaurantDB.ResDBAsyncTask
import com.example.quickfoodie.database.RestaurantDB.RestaurantEntity
import com.example.quickfoodie.models.RestaurantInfo
import java.security.AccessControlContext

class FavouriteFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    var adapterFav: RestaurantAdapter? = null
    lateinit var layoutManagerFav: RecyclerView.LayoutManager
    lateinit var resInfoArray: ArrayList<RestaurantInfo>
    lateinit var progressLayout: RelativeLayout
    lateinit var relativeLayoutFav2: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        //Initialise variables
        initialiseVariables(view)

        progressLayout.visibility = View.VISIBLE

        //fetching data from database
        fetchData()

        //Setting Recycler View
        setRecyclerView()

        return view
    }

    private fun initialiseVariables(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewFav)
        layoutManagerFav = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayoutFav)
        resInfoArray = arrayListOf()
        relativeLayoutFav2 = view.findViewById(R.id.relativeLayoutFav2)
    }

    private fun fetchData() {
        val listResEntity: ArrayList<RestaurantEntity> =
            ResDBAsyncTask(
                activity as Context,
                null,
                4
            ).execute().get()

        //Converting to ResInfo array
        for (resEntity in listResEntity) {
            val resInfo = RestaurantInfo(
                resEntity.res_id,
                resEntity.resName,
                resEntity.resRating,
                resEntity.resPrice,
                resEntity.resImage
            )
            resInfoArray.add(resInfo)
        }
    }

    private fun setRecyclerView() {
        resInfoArray.reverse()
        adapterFav = RestaurantAdapter(activity as Context, resInfoArray)

        if (resInfoArray.size == 0) {
            relativeLayoutFav2.visibility = View.VISIBLE
        } else {
            recyclerView.layoutManager = layoutManagerFav
            recyclerView.adapter = adapterFav
        }

        progressLayout.visibility = View.GONE
    }

}