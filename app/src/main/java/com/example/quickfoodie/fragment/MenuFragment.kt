package com.example.quickfoodie.fragment

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuAdapter
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quickfoodie.R
import com.example.quickfoodie.adapter.MenuFoodAdapter
import com.example.quickfoodie.adapter.RestaurantAdapter
import com.example.quickfoodie.database.menuItemDB.MenuDBAsyncTask
import com.example.quickfoodie.database.menuItemDB.MenuEntity
import com.example.quickfoodie.models.MenuInfo
import com.example.quickfoodie.models.RestaurantInfo
import com.example.quickfoodie.util.ConnectionManager
import kotlinx.android.synthetic.*

class MenuFragment(var actionBar: androidx.appcompat.app.ActionBar) : Fragment() {

    //Declaring variables
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var adapter: MenuFoodAdapter? = null
    lateinit var btnProceedToCart: Button
    lateinit var menuInfoArray: ArrayList<MenuEntity>
    lateinit var progressLayoutMenu: RelativeLayout
    var id: String? = null
    var name: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        //Initialising Variables
        initialiseVariables(view)

        progressLayoutMenu.visibility = View.VISIBLE

        //Fetching data from intent and set it on variable name,id and toolbar
        intentData()

        //Setting data on recycler view
        if (activity != null) {
            if (ConnectionManager().checkConnectivity(activity as Context))
            //Fetch data and set data
                dataFetch()
            else {
                //Set up dialog box
                dialogBoxSetUp()
            }
        }

        //Proceed Button click listener
        proceedButtonSetUp()


        return view
    }

    private fun initialiseVariables(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewDetails)
        layoutManager = LinearLayoutManager(activity)
        btnProceedToCart = view.findViewById(R.id.btnProceedToCart)
        menuInfoArray = arrayListOf()
        progressLayoutMenu = view.findViewById(R.id.progressLayoutMenu)
    }

    private fun intentData() {
        if (activity?.intent != null) {
            id = activity?.intent?.getStringExtra("ResId")
            name = activity?.intent?.getStringExtra("ResName")
        }

        if (activity?.intent == null || id == null || name == null) {
            Toast.makeText(activity, "Error occurs in intent", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
        //Setting title on toolbar
        actionBar.title = name
    }

    private fun dataFetch() {
        val queue = Volley.newRequestQueue(activity)
        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$id"

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {

                            val jsonArray = data.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val newJsonObject = jsonArray.getJSONObject(i)
                                val menuEntity = MenuEntity(
                                    newJsonObject.getString("id"),
                                    newJsonObject.getString("name"),
                                    newJsonObject.getString("cost_for_one")
                                )
                                menuInfoArray.add(menuEntity)
                            }
                            if (activity != null) {

                                //Initialising and calling adapter
                                adapter = MenuFoodAdapter(
                                    activity as Context,
                                    menuInfoArray,
                                    btnProceedToCart
                                )

                                //Giving control to adapter by linking recyclerView with layout manager and adapter
                                if (adapter != null) {
                                    recyclerView.adapter = adapter
                                    recyclerView.layoutManager = layoutManager
                                    setHasOptionsMenu(true)
                                    progressLayoutMenu.visibility = View.GONE
                                }
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
                        fragTransaction.replace(R.id.frameDetails, MenuFragment(actionBar)).commit()
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
        } else {
            dialogBoxSetUp()
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

    private fun proceedButtonSetUp() {
        btnProceedToCart.setOnClickListener {
            //If menu database has some data then only proceed
            if (MenuDBAsyncTask(activity as Context, null, 4, null).execute().get().size != 0) {
                actionBar.title = "My Cart"
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.frameDetails, MyCartFragment())?.commit()
            }
        }
    }
}