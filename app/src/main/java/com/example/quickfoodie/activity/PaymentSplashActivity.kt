package com.example.quickfoodie.activity

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.example.quickfoodie.R

class PaymentSplashActivity : AppCompatActivity() {

    //Declaring variables
    lateinit var imgSplashPayment: ImageView
    lateinit var txtSplashPayment: TextView
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //hiding status bar
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_paymentsplash)


        //Initialise variables
        initialiseVariables()

        //Setting initial value of variables
        imgSplashPayment.scaleX=0f
        imgSplashPayment.scaleY=0f
        txtSplashPayment.alpha=0f

        //Animation
        Handler().postDelayed({
            animateSplash()
        },500)

        //Activity changes after 2.5 sec
        changeActivity()
    }

    fun initialiseVariables(){
        imgSplashPayment=findViewById(R.id.imgSplashPayment)
        txtSplashPayment=findViewById(R.id.txtSplashPayment)
        mediaPlayer= MediaPlayer.create(applicationContext,R.raw.sound)
    }

    private fun animateSplash(){
        imgSplashPayment.animate().scaleX(1f).scaleY(1f).duration=500
        txtSplashPayment.animate().alpha(1f).duration=500
        mediaPlayer.start()
    }

    private fun changeActivity(){
        Handler().postDelayed({
            val intent= Intent(this@PaymentSplashActivity,
                HomeRelatedActivity::class.java)
            startActivity(intent)
            finish()
        },2500)
    }

    override fun onBackPressed() {
        //Do nothing on pressing back
    }
}