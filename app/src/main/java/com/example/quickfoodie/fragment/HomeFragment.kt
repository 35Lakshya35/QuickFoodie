package com.example.quickfoodie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quickfoodie.R
import com.example.quickfoodie.adapter.RestaurantAdapter
import com.example.quickfoodie.database.menuItemDB.MenuDBAsyncTask
import com.example.quickfoodie.models.RestaurantInfo
import com.example.quickfoodie.util.ConnectionManager
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    //Declaring Variables
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManagerHome: RecyclerView.LayoutManager
    lateinit var adapterHome: RestaurantAdapter
    lateinit var resInfoArray: ArrayList<RestaurantInfo>
    lateinit var progressLayout: RelativeLayout
    var olderChoice=-1

    var ratingComparator = Comparator<RestaurantInfo> { Res1, Res2 ->
        if ((Res1.ResRating).compareTo(Res2.ResRating) == 0) {
            (Res2.ResName).compareTo(Res1.ResName)
        } else
            (Res1.ResRating).compareTo(Res2.ResRating)
    }

    var costComparator = Comparator<RestaurantInfo> { Res1, Res2 ->
        if ((Res1.ResPrice).compareTo(Res2.ResPrice) == 0) {
            (Res1.ResName).compareTo(Res2.ResName)
        } else
            (Res1.ResPrice).compareTo(Res2.ResPrice)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        //initialising variables
        initialiseVariables(view)

        progressLayout.visibility = View.VISIBLE

        if (activity != null) {
            if (ConnectionManager().checkConnectivity(activity as Context))
            //Fetch data and set data
                dataFetch()
            else {
                //Set up dialog box
                dialogBoxSetUp()
            }
        }
        return view
    }

    private fun initialiseVariables(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewHome)
        layoutManagerHome = LinearLayoutManager(activity)
        resInfoArray = arrayListOf()
        progressLayout = view.findViewById(R.id.progressLayoutHome)
    }

    private fun dataFetch() {
        val queue = Volley.newRequestQueue(activity)
        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    progressLayout.visibility = View.GONE

                    try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")

                    if (success) {
                            val jsonArray = data.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val newJsonObject = jsonArray.getJSONObject(i)
                                val resInfo = RestaurantInfo(
                                    newJsonObject.getString("id"),
                                    newJsonObject.getString("name"),
                                    newJsonObject.getString("rating"),
                                    newJsonObject.getString("cost_for_one"),
                                    newJsonObject.getString("image_url")
                                )
                                resInfoArray.add(resInfo)
                            }
                            if (activity != null) {
                                //Initialising and calling adapter
                                adapterHome = RestaurantAdapter(activity as Context, resInfoArray)

                                //Giving control to adapter by linking recyclerView with layout manager and adapter
                                recyclerView.adapter = adapterHome
                                recyclerView.layoutManager = layoutManagerHome
                                setHasOptionsMenu(true)
                            }
                    } else {
                        showMsg("Getting false response of success")
                    }
                    } catch (e: Exception) {
                    showMsg("Json Error Occurred")
                }

                }, Response.ErrorListener {
                    //Due to slow internet reopen the home page
                    if (activity != null) {
                        showMsg("Slow Internet Connection")
                        val activity1 = activity as AppCompatActivity
                        val fragTransaction = activity1.supportFragmentManager.beginTransaction()
                        //Reopening Home Page
                        fragTransaction.replace(R.id.frameLayout, HomeFragment()).commit()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "b4e3c5595f831b"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        }

    }


    private fun dialogBoxSetUp() {
        //Connection is not available
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
        builder.setTitle("Error")
        builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { _, _ ->
            ActivityCompat.finishAffinity(activity as Activity)
        }
        builder.create()
        builder.show()
    }

    fun showMsg(msg: String) {
        if (activity != null)
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    //Creating Search and sort icon on toolbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
    }

    //Functionality of toolbar search and sort icons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menuSort -> {
                val dialog=AlertDialog.Builder(activity)
                dialog.setTitle("Sort By:")
                dialog.setSingleChoiceItems(R.array.sortOptions,olderChoice,DialogInterface.OnClickListener { dialogInterface, i ->
                    olderChoice=i
                    notifyAdapter(olderChoice)
                    dialogInterface.dismiss()
                })
                dialog.create()
                dialog.show()

            }
            R.id.menuSearch -> {
                val searchView: SearchView = item.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        olderChoice=-1
                        adapterHome?.filter?.filter(newText)
                        return false
                    }
                })
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun notifyAdapter(i:Int){
        when(i){
            0->{//Low to High price sort
                Collections.sort(resInfoArray,costComparator)
            }
            1->{//High to low price sort
                Collections.sort(resInfoArray,costComparator)
                resInfoArray.reverse()
            }
            2->{
                Collections.sort(resInfoArray,ratingComparator)
                resInfoArray.reverse()
            }
        }
        adapterHome?.notifyDataSetChanged()
    }


}