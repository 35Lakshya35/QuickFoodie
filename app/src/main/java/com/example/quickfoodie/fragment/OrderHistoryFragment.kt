package com.example.quickfoodie.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quickfoodie.R
import com.example.quickfoodie.adapter.PreviousOrderAdapter
import com.example.quickfoodie.database.menuItemDB.MenuEntity
import com.example.quickfoodie.models.OrderInfo
import com.example.quickfoodie.util.ConnectionManager
import org.json.JSONObject

class OrderHistoryFragment : Fragment() {

    //Declaring variables
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: PreviousOrderAdapter
    lateinit var orderInfoArray: ArrayList<OrderInfo>
    lateinit var sp: SharedPreferences
    var userId: String = ""
    lateinit var progressBarOrderHistory: RelativeLayout
    lateinit var layoutNoOrderPlaced: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        //Initialise Variables
        initialiseVariables(view)

        progressBarOrderHistory.visibility = View.VISIBLE

        //data fetch and set on recycler view
        dataFetch()

        return view
    }

    private fun initialiseVariables(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewOrder)

        orderInfoArray = arrayListOf()
        progressBarOrderHistory = view.findViewById(R.id.progressBarOrderHistory)
        layoutNoOrderPlaced = view.findViewById(R.id.layoutNoOrderPlaced)
        sp = activity?.getSharedPreferences(
            getString(R.string.spFile),
            Context.MODE_PRIVATE
        ) as SharedPreferences

        //Fetching userId from shared preference file
        val strJson = sp.getString("login details", "No Data")
        if (strJson != "No Data") {
            val jsonObj = object : JSONObject(strJson as String) {}
            userId = jsonObj.getString("user_id")
        }
    }

    private fun dataFetch() {
        val queue = Volley.newRequestQueue(activity)
        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    progressBarOrderHistory.visibility = View.GONE

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {

                            //Fetching data as json object
                            val jsonArray = data.getJSONArray("data")

                            //Setting data in orderInfoArray array
                            for (i in 0 until jsonArray.length()) {

                                val menuEntityList = arrayListOf<MenuEntity>()
                                val newJsonObject = jsonArray.getJSONObject(i)
                                val menuJsonArray = newJsonObject.getJSONArray("food_items")
                                for (j in 0 until menuJsonArray.length()) {
                                    val jsonMenu = menuJsonArray.getJSONObject(j)
                                    val menuEntity = MenuEntity(
                                        jsonMenu.getString("food_item_id"),
                                        jsonMenu.getString("name"),
                                        jsonMenu.getString("cost")
                                    )
                                    menuEntityList.add(menuEntity)
                                }

                                val orderInfo = OrderInfo(
                                    newJsonObject.getString("restaurant_name"),
                                    setDateAndTime(newJsonObject.getString("order_placed_at")),
                                    menuEntityList
                                )
                                orderInfoArray.add(orderInfo)
                            }

                            //Connection with recycler view ,adapter and layout manager
                            if (activity != null) {
                                adapter = PreviousOrderAdapter(activity as Context, orderInfoArray)

                                val layoutManager = LinearLayoutManager(activity)
                                recyclerView.adapter = adapter
                                recyclerView.layoutManager = layoutManager

                                //no order placed screen
                                if (orderInfoArray.size == 0) {
                                    layoutNoOrderPlaced.visibility = View.VISIBLE
                                }
                            }

                        } else {
                            showMsg("Getting false response of success")
                        }
                    } catch (e: Exception) {
                        showMsg("Json Error Occurred")
                    }

                }, Response.ErrorListener {
                    //Due to slow internet reopen the Order history page
                    if (activity != null) {
                        showMsg("Slow Internet Connection")
                        val activity1 = activity as AppCompatActivity
                        val fragTransaction = activity1.supportFragmentManager.beginTransaction()
                        //Reopening Home Page
                        fragTransaction.replace(R.id.frameLayout, OrderHistoryFragment()).commit()
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

    fun showMsg(msg: String) {
        if (activity != null)
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    //Formatting date and time
    fun setDateAndTime(str: String): String {
        val array = str.split(" ")
        var hours = array[1].subSequence(0, 2).toString()
        val meridiem: String
        when {
            hours > "12" -> {
                hours = (hours.toInt() - 12).toString()
                if (hours.toInt() < 10)
                    hours = "0$hours"
                meridiem = " PM"
            }
            hours == "00" -> {
                hours = "12"
                meridiem = " AM"
            }
            hours == "12" -> meridiem = " PM"
            else -> meridiem = " AM"
        }
        val time = hours + ":" + array[1].subSequence(3, 5).toString() + meridiem
        return "${array[0]}\n$time"
    }
}