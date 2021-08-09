package com.example.quickfoodie.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.quickfoodie.R
import com.google.gson.JsonObject
import org.json.JSONObject

class ProfileFragment : Fragment() {

    //Declaring Variables
    lateinit var txtProfileName: TextView
    lateinit var txtProfileNumber: TextView
    lateinit var txtProfileEmail: TextView
    lateinit var txtProfileAddress: TextView
    lateinit var sp: SharedPreferences
    lateinit var spData: JSONObject

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //Initialise variables
        initialiseVariables(view)

        //Fetching details from shared preference in store it in spData(JSONObject)
        fetchDetails()

        //Setting data on variables
        setDataOnTextViews()

        return view
    }

    fun initialiseVariables(view: View) {
        txtProfileName = view.findViewById(R.id.txtProfileName)
        txtProfileNumber = view.findViewById(R.id.txtProfileNumber)
        txtProfileEmail = view.findViewById(R.id.txtProfileEmail)
        txtProfileAddress = view.findViewById(R.id.txtProfileAddress)
        sp = activity?.getSharedPreferences(
            getString(R.string.spFile),
            Context.MODE_PRIVATE
        ) as SharedPreferences
    }

    private fun fetchDetails() {
        val strJson = sp.getString("login details", "No Data")
        if (strJson != "No Data") {
            spData = object : JSONObject(strJson as String) {}
        }
    }

    private fun setDataOnTextViews(){
        txtProfileName.text=spData.getString("name")
        txtProfileNumber.text=spData.getString("mobile_number")
        txtProfileEmail.text=spData.getString("email")
        txtProfileAddress.text=spData.getString("address")
    }
}