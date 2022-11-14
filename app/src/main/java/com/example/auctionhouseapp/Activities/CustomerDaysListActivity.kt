package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.auctionhouseapp.Fragments.AuctionDaysListFragment
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.CustomerDaysListFragment
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CustomerDaysListActivity : AppCompatActivity() {
    val House: AuctionHouse = AuctionHouse()
    val LoadingFragment = AuctionDaysSpinner()
    val List = CustomerDaysListFragment()
    lateinit var HouseId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_days_list)
        HouseId = intent.getStringExtra("HouseId") as String
        House.FetchHouseData(HouseId, ::setHouseDaysOnScreen)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewAuctionDays,LoadingFragment)
            commit()
        }
        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }

    }
    fun setHouseDaysOnScreen() {
        List.House = House
        List.HouseId = HouseId
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewAuctionDays,List)
            commit()
        }

    }
}