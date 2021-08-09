package com.example.quickfoodie.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quickfoodie.R
import com.example.quickfoodie.activity.HomeRelatedActivity
import com.example.quickfoodie.util.ConnectionManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import java.lang.Exception

class RegisterFragment : Fragment() {

    //Declaring variables
    lateinit var etUserName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobNo: EditText
    lateinit var etDelivery: EditText
    lateinit var etPassword: EditText
    lateinit var etCPassword: EditText
    lateinit var toolbar: Toolbar
    lateinit var btnRegister: Button
    lateinit var sp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        //Adjusting screen size to make screen scrollable
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        //Initialing Variables
        initialiseVariables(view)

        //Setting Toolbar
        setToolbar()

        //Register Button Click Listener
        registerClickListener()

        return view
    }

    private fun initialiseVariables(view: View) {
        etUserName = view.findViewById(R.id.etUserName)
        etEmail = view.findViewById(R.id.etEmail)
        etMobNo = view.findViewById(R.id.etMobileNumberRegister)
        etDelivery = view.findViewById(R.id.etDeliveryAddress)
        etPassword = view.findViewById(R.id.etPassword1Register)
        etCPassword = view.findViewById(R.id.etPassword2Register)
        toolbar = view.findViewById(R.id.toolbarRegister)
        btnRegister = view.findViewById(R.id.btnRegister)
        sp = (activity as Activity).getSharedPreferences(
            getString(R.string.spFile),
            Context.MODE_PRIVATE
        )
    }

    private fun setToolbar() {
        val activity1 = activity as AppCompatActivity
        activity1.setSupportActionBar(toolbar)
        activity1.supportActionBar?.title = "Register Yourself"
        activity1.supportActionBar?.setHomeButtonEnabled(true)
        activity1.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun registerClickListener() {

        btnRegister.setOnClickListener {

            //Condition for stop registrations
            if (etUserName.text.isEmpty() || etEmail.text.isEmpty() || etMobNo.text.isEmpty() || etDelivery.text.isEmpty() || etPassword.text.isEmpty() || etCPassword.text.isEmpty()) {
                displayMsg("Please Fill all Details")
            }
            else if (etPassword.text.toString() != etCPassword.text.toString()) {
                displayMsg("Both passwords are different")
                etPassword.setText("")
                etCPassword.setText("")
            }
            else if(etPassword.text.toString().length<4){
                displayMsg("Password length is less than 4")
                etPassword.setText("")
                etCPassword.setText("")
            }
            else if (!ConnectionManager().checkConnectivity(activity as Context)) {
                displayMsg("No internet connection")
            }

            //Condition for proceed registration
            else {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val jsonObj = JSONObject()
                jsonObj.put("name", etUserName.text)
                jsonObj.put("mobile_number", etMobNo.text)
                jsonObj.put("password", etPassword.text)
                jsonObj.put("address", etDelivery.text)
                jsonObj.put("email", etEmail.text)

                val jsonObjectRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonObj,

                        Response.Listener {
                            try {
                                val jsonObj1 = it.getJSONObject("data")
                                val success = jsonObj1.getBoolean("success")

                                if (success) {
                                    val jsonObjSuccess = jsonObj1.getJSONObject("data")
                                    //Converting jsonObjSuccess to String and store it in shared preference file
                                    val strJson = jsonObjSuccess.toString()
                                    sp.edit().putString("login details", strJson).apply()
                                    sp.edit().putBoolean("login status", true).apply()
                                    startActivity(Intent(activity, HomeRelatedActivity::class.java))
                                    (activity as AppCompatActivity).finish()

                                } else {
                                    displayMsg(jsonObj1.getString("errorMessage"))
                                }
                            } catch (e: Exception) {
                                displayMsg("Json Error Occurred")
                            }
                        },

                        Response.ErrorListener {
                            displayMsg("Volley Error Occurred\ndue to slow internet connectivity")
                        }

                    ) {
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
    }

    fun displayMsg(msg: String) {
        if (activity != null) {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    //Clicking On Top Back Button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.LoginFrame, LoginFragment())?.commit()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        //To counter the above declaration for setting screen size according to keyboard to make screen scrollable
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.MATCH_PARENT)
        super.onDestroyView()
    }
}
