package com.example.quickfoodie.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBar
import com.example.quickfoodie.R
import com.example.quickfoodie.database.menuItemDB.MenuDBAsyncTask
import com.example.quickfoodie.fragment.MenuFragment
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuCartActivity : AppCompatActivity() {

    //Declaring variables
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    var id: String? = null
    var name:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_cart)

        //initialising variables
        initialisingVariables()

        //setting toolbar
        toolbarSetUp()

        //Open restaurant menu
        openMenu()
    }

    private fun initialisingVariables() {
        toolbar = findViewById(R.id.toolbarDetails)
        frameLayout = findViewById(R.id.frameDetails)
    }

    private fun toolbarSetUp() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openMenu() {
        var actionBar=supportActionBar
        supportFragmentManager.beginTransaction().replace(R.id.frameDetails, MenuFragment(actionBar as ActionBar))
            .commit()
    }

    override fun onBackPressed() {
        backButtonFunctionality()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        backButtonFunctionality()
        return super.onOptionsItemSelected(item)
    }

    private fun backButtonFunctionality(){
        val frag=supportFragmentManager.findFragmentById(R.id.frameDetails)
        if(frag !is MenuFragment){
            openMenu()
        }
        else{
            if(btnProceedToCart.visibility== View.VISIBLE)
                dialogBoxSetUp()
            else
                openHomePage()
        }
    }

    private fun openHomePage(){
        val intent= Intent(this@MenuCartActivity,HomeRelatedActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun dialogBoxSetUp() {
        //Connection is not available
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Going back will reset cart items.Do you still want to proceed?")
        builder.setPositiveButton("YES") { _, _ ->
            openHomePage()
        }
        builder.setNegativeButton("NO"){_,_->
            //Do nothing just cancel the dialog box
        }
        builder.create()
        builder.show()
    }
}