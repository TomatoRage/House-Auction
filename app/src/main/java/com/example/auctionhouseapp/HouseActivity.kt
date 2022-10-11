package com.example.auctionhouseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HouseActivity : AppCompatActivity() {

    val House:AuctionHouse = AuctionHouse()
    val LoadingFragment = AuctionDaysSpinner()
    val List = AuctionDaysListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auctionhouse)

        House.FetchHouseData(Firebase.auth.currentUser!!.uid,::PerformAfterData)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,LoadingFragment)
            commit()
        }

        findViewById<Button>(R.id.btn_add_day).setOnClickListener {
            //TODO: Navigate to add day screen
        }


    }

    fun PerformAfterData() {

        List.House = House

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,List)
            commit()
        }

    }
}