package com.example.auctionhouseapp.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.auctionhouseapp.*
import com.example.auctionhouseapp.Fragments.AuctionDaysListFragment
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.CustomerProfile
import com.example.auctionhouseapp.Fragments.HouseProfile
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HouseActivity : AppCompatActivity() {

    val House: AuctionHouse = AuctionHouse()
    val LoadingFragment = AuctionDaysSpinner()
    val List = AuctionDaysListFragment()
    lateinit var resultLauncher:ActivityResultLauncher<Intent>

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auctionhouse)

        House.Days.clear()
        House.FetchHouseData(Firebase.auth.currentUser!!.uid,::PerformAfterData)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,LoadingFragment)
            commit()
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainerView,LoadingFragment)
                    commit()
                }
            }
        }

        findViewById<Button>(R.id.btn_add_day).setOnClickListener {

            val intent = Intent(applicationContext, CreateDayActivity::class.java)
            intent.putExtra("House", House.GetUID())
            resultLauncher.launch(intent)
        }

        findViewById<Button>(R.id.btn_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<BottomNavigationView>(R.id.bottomNavHouse).setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.houseProfile -> {
                    var profile = HouseProfile()
                    profile.my_house = House
                    ReplaceFragment(profile)
                }
                R.id.auctionDaysListFragment -> {
                    PerformAfterData()
                }
            }
            true
        }

    }

    fun ReplaceFragment(fragment:Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

    fun PerformAfterData() {

        if(supportFragmentManager.isDestroyed)
            return

        List.House = House

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,List)
            commit()
        }

    }
}