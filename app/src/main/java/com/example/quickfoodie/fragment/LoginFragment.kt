package com.example.quickfoodie.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quickfoodie.R
import com.example.quickfoodie.activity.HomeRelatedActivity
import com.example.quickfoodie.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class LoginFragment : Fragment() {

    //Declaring variables
    lateinit var btnLogin: Button
    lateinit var etMobNo: EditText
    lateinit var etPassword: EditText
    lateinit var txtForget: TextView
    lateinit var txtRegister: TextView
    lateinit var sp:SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        //Initialising Variables
        initialiseVariable(view)

        //Open home page directly if login status is true
        if(sp.getBoolean("login status",false)){
            openHome()
        }

        //Onclick Listener of Forget Password
        forgetPasswordListener()

        //Onclick Listener of Registration
        newUserRegistration()

        //Onclick Listener of Login Button
        login()
        return view
    }

    private fun initialiseVariable(view: View) {
        btnLogin = view.findViewById(R.id.btnLogin)
        etMobNo = view.findViewById(R.id.etMobNoLogin)
        etPassword = view.findViewById(R.id.etPasswordLogin)
        txtForget = view.findViewById(R.id.txtForgetPassword)
        txtRegister = view.findViewById(R.id.txtRegisterInLogin)
        sp = (activity as Activity).getSharedPreferences(getString(R.string.spFile),Context.MODE_PRIVATE)
    }

    private fun forgetPasswordListener() {
        txtForget.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.LoginFrame, ForgetFragment())?.commit()
        }
    }

    private fun newUserRegistration() {
        txtRegister.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.LoginFrame, RegisterFragment())?.commit()
        }
    }

    private fun login() {
        btnLogin.setOnClickListener {

            //Condition for stop login
            if (etMobNo.text.isEmpty() || etPassword.text.isEmpty()) {
                displayMsg("Please Fill all Details")
            } else if (!ConnectionManager().checkConnectivity(activity as Context)) {
                displayMsg("No internet connection")
            }

            //Condition for proceed login
            else {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val jsonObj = JSONObject()
                jsonObj.put("mobile_number", etMobNo.text)
                jsonObj.put("password", etPassword.text)

                val jsonObjectRequest =
                    object : JsonObjectRequest(
                        Request.Method.POST, url, jsonObj,

                        Response.Listener {
                            try {
                                val jsonObj1 = it.getJSONObject("data")
                                val success = jsonObj1.getBoolean("success")

                                if (success) {
                                    val obj = jsonObj1.getJSONObject("data")

                                    //Converting json obj to String and store it in shared preference file
                                    val strJson = obj.toString()
                                    sp.edit().putString("login details", strJson).apply()
                                    sp.edit().putBoolean("login status", true).apply()

                                    openHome()

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

    private fun openHome(){
        val intent=Intent(activity,HomeRelatedActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}