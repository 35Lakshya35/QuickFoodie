package com.example.quickfoodie.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quickfoodie.R
import com.example.quickfoodie.fragment.LoginFragment

class LoginRelatedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_related)

        //Open Login Page
        openLoginPage()
    }

    private fun openLoginPage(){
        supportFragmentManager.beginTransaction().replace(R.id.LoginFrame,LoginFragment()).commit()
    }

    //On pressing back button open login page or exit the app
    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.LoginFrame)
        if(frag !is LoginFragment){
            openLoginPage()
        }
        else{
            super.onBackPressed()
        }
    }
}