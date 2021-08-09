package com.example.quickfoodie.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.example.quickfoodie.R

class SplashActivity : AppCompatActivity() {

    //Declaring variables
    lateinit var imgSplashLogin:ImageView
    lateinit var txtSplashLogin:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //hiding status bar
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash)

        //Initialising Variables
        initialiseVariables()

        //animation
        animateSplash()

        //Activity changes after 2 sec
        changeActivity()
    }

    fun initialiseVariables(){
        imgSplashLogin=findViewById(R.id.imgSplashLogin)
        txtSplashLogin=findViewById(R.id.txtSplashLogin)
    }

    private fun animateSplash(){
        imgSplashLogin.alpha=0f
        imgSplashLogin.translationY=-300f
        txtSplashLogin.alpha=0f
        txtSplashLogin.translationY=300f

        imgSplashLogin.animate().translationYBy(300f).alpha(1f).duration = 1900
        txtSplashLogin.animate().translationYBy(-300f).alpha(1f).duration = 1900
    }

    private fun changeActivity(){
        Handler().postDelayed({
            val intent= Intent(this@SplashActivity,
                LoginRelatedActivity::class.java)
            startActivity(intent)
            finish()
        },2000)
    }

    override fun onBackPressed() {
        //Do nothing on pressing back
    }
}