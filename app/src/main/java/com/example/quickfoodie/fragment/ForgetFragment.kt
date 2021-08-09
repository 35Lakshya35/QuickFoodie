package com.example.quickfoodie.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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

class ForgetFragment : Fragment() {

    //Initialising Variables
    lateinit var etMobNo: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button
    lateinit var sp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forget, container, false)

        //Adjusting screen size to make screen scrollable
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        //Initialising Variables
        initialiseVariables(view)

        //Onclick Listener of next button
        next()


        return view
    }

    private fun initialiseVariables(view: View) {
        etMobNo = view.findViewById(R.id.etMobNoForget)
        etEmail = view.findViewById(R.id.etEmailForget)
        btnNext = view.findViewById(R.id.btnNext)
        sp = (activity as Activity).getSharedPreferences(
            getString(R.string.spFile),
            Context.MODE_PRIVATE
        )
    }

    private fun next() {
        btnNext.setOnClickListener {
            //Condition for stop processing next
            if (etMobNo.text.isEmpty() || etEmail.text.isEmpty()) {
                displayMsg("Please Fill all Details")
            } else if (!ConnectionManager().checkConnectivity(activity as Context)) {
                displayMsg("No internet connection")
            }

            //Condition for proceed next
            else {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonObj = JSONObject()
                jsonObj.put("mobile_number", etMobNo.text)
                jsonObj.put("email", etEmail.text)

                val jsonObjectRequest =
                    object : JsonObjectRequest(
                        Request.Method.POST, url, jsonObj,

                        Response.Listener {
                            try {
                                val jsonObj1 = it.getJSONObject("data")
                                val success = jsonObj1.getBoolean("success")

                                if (success) {

                                    if (jsonObj1.getBoolean("first_try")) {
                                        displayMsg("OTP is sent to your Registered Email")
                                    } else {
                                        displayMsg("Note:- Please use previous OTP provided")
                                    }
                                    //Open Reset Page
                                    if (activity != null)
                                        activity?.supportFragmentManager?.beginTransaction()
                                            ?.replace(
                                                R.id.LoginFrame,
                                                ResetFragment(etMobNo.text.toString())
                                            )?.commit()
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
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        //To counter the above declaration for setting screen size according to keyboard to make screen scrollable
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.MATCH_PARENT)
        super.onDestroyView()
    }

}