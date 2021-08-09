package com.example.quickfoodie.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quickfoodie.R
import com.example.quickfoodie.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class ResetFragment(var mobile_number: String) : Fragment() {

    //Declaring Variables
    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reset, container, false)

        //Initialising Variables
        initialiseVariables(view)

        //Onclick Listener of submit button
        submitResponse()

        return view
    }

    private fun initialiseVariables(view: View) {
        etOTP = view.findViewById(R.id.etOTP)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnSubmit = view.findViewById(R.id.btnSubmitReset)
    }

    private fun submitResponse() {
        btnSubmit.setOnClickListener {
            //Condition for stop processing submit
            if (etOTP.text.isEmpty() || etNewPassword.text.isEmpty() || etConfirmPassword.text.isEmpty()) {
                displayMsg("Please Fill all Details")
            } else if (!ConnectionManager().checkConnectivity(activity as Context)) {
                displayMsg("No internet connection")
            } else if (etNewPassword.text.toString() != etConfirmPassword.text.toString()) {
                displayMsg("Both filled passwords are different")
                etNewPassword.setText("")
                etConfirmPassword.setText("")
            } else if (etNewPassword.text.toString().length < 4) {
                displayMsg("Entered password length is less than four")
                etNewPassword.setText("")
                etConfirmPassword.setText("")
            }

            //Condition for proceed submit
            else {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonObj = JSONObject()
                jsonObj.put("mobile_number", mobile_number)
                jsonObj.put("password", etNewPassword.text.toString())
                jsonObj.put("otp", etOTP.text.toString())

                val jsonObjectRequest =
                    object : JsonObjectRequest(
                        Request.Method.POST, url, jsonObj,

                        Response.Listener {
                            try {
                                val jsonObj1 = it.getJSONObject("data")
                                val success = jsonObj1.getBoolean("success")

                                if (success) {
                                    displayMsg(jsonObj1.getString("successMessage"))

                                    //Open Home Page
                                    activity?.supportFragmentManager?.beginTransaction()
                                        ?.replace(R.id.LoginFrame, LoginFragment())?.commit()
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

}