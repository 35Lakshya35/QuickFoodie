package com.example.quickfoodie.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.quickfoodie.R
import com.example.quickfoodie.database.RestaurantDB.ResDBAsyncTask
import com.example.quickfoodie.database.menuItemDB.MenuDBAsyncTask
import com.example.quickfoodie.fragment.*
import com.example.quickfoodie.util.ConnectionManager
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject

class HomeRelatedActivity : AppCompatActivity() {

    //Declaring Variables
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var txtPersonName: TextView
    lateinit var txtPersonMobNo: TextView
    lateinit var sp: SharedPreferences
    var previousChecked: MenuItem? = null
    lateinit var jsonObj: JSONObject

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_related)

        //Initialing variables
        initialiseVariables()

        //Clearing menu cart item database
        clearMenuCartDatabase()

        //Setting navigation header and initialise its variables
        navigationHeaderSetUp()

        //Fetching user data from shared preference file and set it to drawer header
        val strJson = sp.getString("login details", "No Data")
        if (strJson != "No Data") {
            jsonObj = object : JSONObject(strJson as String) {}

            //Setting user details on Drawer header
            txtPersonName.text = jsonObj.getString("name")
            txtPersonMobNo.text = "+91-" + jsonObj.getString("mobile_number")
        }

        //Creating Toolbar
        toolbarSetUp()

        //Creating Hamburger Icon
        hamburgerIcon()

        //Open Home page
        openHome()

        //Navigation item click listener
        navigationItemClickListener()


    }

    private fun initialiseVariables() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbarHome)
        frameLayout = findViewById(R.id.frameLayout)
        sp = getSharedPreferences(getString(R.string.spFile), Context.MODE_PRIVATE)
    }

    private fun clearMenuCartDatabase() {
        val menuList = MenuDBAsyncTask(this, null, 4, null).execute().get()
        MenuDBAsyncTask(this, null, 5, menuList).execute().get()
    }

    private fun navigationHeaderSetUp() {
        val hView = navigationView.inflateHeaderView(R.layout.drawer_header)
        txtPersonName = hView.findViewById(R.id.txtPersonName)
        txtPersonMobNo = hView.findViewById(R.id.txtPersonMobNo)
    }

    private fun toolbarSetUp() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun hamburgerIcon() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,R.string.openDrawer,R.string.closedDrawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun openHome() {
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.menuHome)
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, HomeFragment()).commit()
    }

    //Functionality of Hamburger home button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigationItemClickListener() {
        navigationView.setNavigationItemSelectedListener {
            var drawerClosingTime=100L
            var fragOpeningTime=0L

            if(it.itemId==R.id.menuHome || it.itemId==R.id.menuOrderHistory){
                drawerClosingTime=0L
                fragOpeningTime=100L
            }

            val pendingDrawerClose = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(pendingDrawerClose, drawerClosingTime)
//            drawerLayout.closeDrawers()

            if (previousChecked != null) {
                previousChecked?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousChecked = it


            val fragTransaction = supportFragmentManager.beginTransaction()
            val pendingFragmentOpen = Runnable {
                if (ConnectionManager().checkConnectivity(this)) {
                    when (it.itemId) {
                        R.id.menuHome -> {
                            openHome()
                        }
                        R.id.menuProfile -> {
                            supportActionBar?.title = "My Profile"
                            fragTransaction.replace(R.id.frameLayout, ProfileFragment()).commit()
                        }
                        R.id.menuFavourite -> {
                            supportActionBar?.title = "Favourites Restaurants"
                            fragTransaction.replace(R.id.frameLayout, FavouriteFragment()).commit()
                        }
                        R.id.menuFAQs -> {
                            supportActionBar?.title = "FAQs"
                            fragTransaction.replace(R.id.frameLayout, FAQsFragment()).commit()
                        }
                        R.id.menuOrderHistory -> {
                            supportActionBar?.title = "Order History"
                            fragTransaction.replace(R.id.frameLayout, OrderHistoryFragment())
                                .commit()
                        }
                        R.id.menuLogOut -> {

                            //Exiting home
                            val builder = AlertDialog.Builder(this@HomeRelatedActivity)
                            builder.setTitle("Confirmation")
                                .setMessage("Are you sure you want exit?")
                                .setPositiveButton("Yes") { _, _ ->

                                    //clearing shared preference
                                    sp.edit().clear().apply()

                                    //clearing fav. restaurant database
                                    val listAll = ResDBAsyncTask(this, null, 4).execute().get()
                                    for (listEntity in listAll)
                                        ResDBAsyncTask(this, listEntity, 2).execute().get()

                                    //moving to login page
                                    startActivity(Intent(this@HomeRelatedActivity,LoginRelatedActivity::class.java))
                                    finish()
                                }
                                .setNegativeButton("No") { _, _ ->
                                    //Do nothing
                                }
                                .create()
                                .show()
                        }
                    }
                } else
                    dialogBoxSetUp()
            }
            Handler().postDelayed(pendingFragmentOpen, fragOpeningTime)

            return@setNavigationItemSelectedListener true
        }
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if (frag !is HomeFragment) {
            previousChecked?.isChecked = false
            openHome()
        } else
            super.onBackPressed()
    }

    private fun dialogBoxSetUp() {
        //Connection is not available
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { _, _ ->
            ActivityCompat.finishAffinity(this)
        }
        builder.create()
        builder.show()
    }
}