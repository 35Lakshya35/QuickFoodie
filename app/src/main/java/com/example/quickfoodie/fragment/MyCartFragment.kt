package com.example.quickfoodie.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quickfoodie.R
import com.example.quickfoodie.activity.HomeRelatedActivity
import com.example.quickfoodie.activity.PaymentSplashActivity
import com.example.quickfoodie.adapter.CartAdapter
import com.example.quickfoodie.adapter.MenuFoodAdapter
import com.example.quickfoodie.adapter.RestaurantAdapter
import com.example.quickfoodie.database.menuItemDB.MenuDBAsyncTask
import com.example.quickfoodie.database.menuItemDB.MenuEntity
import com.example.quickfoodie.models.RestaurantInfo
import com.example.quickfoodie.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class MyCartFragment : Fragment() {

    //Declaring Variables
    lateinit var txtItemNameCart: TextView
    lateinit var foodList: ArrayList<MenuEntity>
    lateinit var btnPlaceOrder: Button
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CartAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var sp: SharedPreferences
    private var totalCost: Int = 0
    lateinit var progressBarCart:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        //Initialise variables
        initialiseVariables(view)

        //Setting heading on toolbar
        if (activity?.intent != null) {
            txtItemNameCart.text = activity?.intent?.getStringExtra("ResName")
        }

        //Setting recycler view
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        //Setting text on button
        buttonSetText()

        //Button place order functionality
        placeOrder()


        return view
    }

    private fun initialiseVariables(view: View) {
        txtItemNameCart = view.findViewById(R.id.txtItemNameCart)
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder)
        recyclerView = view.findViewById(R.id.recyclerViewCart)
        layoutManager = LinearLayoutManager(activity)
        //Fetching data from database
        foodList = MenuDBAsyncTask(activity as Context, null, 4, null).execute().get()
        adapter = CartAdapter(activity as Context, foodList)
        sp = (activity as Activity).getSharedPreferences(
            getString(R.string.spFile),
            Context.MODE_PRIVATE
        )
        progressBarCart=view.findViewById(R.id.progressBarCart)
    }

    @SuppressLint("SetTextI18n")
    private fun buttonSetText() {
        for (menuEntity in foodList) {
            totalCost += menuEntity.menuPrice?.toInt() as Int
        }
        btnPlaceOrder.setText("Place Order(Total:Rs$totalCost)")
    }

    private fun placeOrder() {
        btnPlaceOrder.setOnClickListener {
            progressBarCart.visibility=View.VISIBLE
            btnPlaceOrder.text=""
            val queue = Volley.newRequestQueue(activity)
            if (ConnectionManager().checkConnectivity(activity as Context)) {

                val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                val jsonObject = parameterJsonObject()

                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonObject, Response.Listener {
//                        progressLayout.visibility = View.GONE

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {
                                openPaymentSplashActivity()
                            } else {
                                openHomePageDirectly()
                            }

                        }, Response.ErrorListener {
                            if (activity != null)
                                Toast.makeText(activity, "Slow internet Connectivity\nPayment Fails", Toast.LENGTH_SHORT).show()
                            openHomePageDirectly()
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
            else{
                dialogBoxSetUp()
            }
        }
    }

    private fun parameterJsonObject(): JSONObject {

        var user_id: String = ""
        var restaurant_id: String = ""
        var total_cost: String = ""
        val food = JSONArray()
        val result = JSONObject()


        //1.setting user_id
        //Fetching user data from shared preference file
        val strJson = sp.getString("login details", "No Data")
        //Converting string to json object
        var jsonObj = object : JSONObject(strJson as String) {}
        user_id = jsonObj.getString("user_id")


        //2.setting restaurant_id
        if (activity?.intent != null) {
            restaurant_id = activity?.intent?.getStringExtra("ResId") as String
        }

        //3.setting total_cost
        total_cost = totalCost.toString()

        //4.setting food
        for (menuEntity in foodList) {
            val tempObj = JSONObject()
            tempObj.put("food_item_id", menuEntity.menu_id)
            food.put(tempObj)
        }
        println(food.toString())

        //5. setting result
        result.put("user_id", user_id)
        result.put("restaurant_id", restaurant_id)
        result.put("total_cost", total_cost)
        result.put("food", food)

        println(result)

        return result
    }

    fun openHomePageDirectly(){
        Toast.makeText(activity,"Sorry some unexpected error occurred\nPayment Fails",Toast.LENGTH_LONG).show()
        val intent=Intent(activity,HomeRelatedActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    fun openPaymentSplashActivity(){
        activity?.startActivity(Intent(activity,PaymentSplashActivity::class.java))
        activity?.finish()
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
}