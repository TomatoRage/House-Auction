package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.auctionhouseapp.AuctionDayStatus
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.CustomerDaysListFragment
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.google.firebase.auth.FirebaseAuth


class CustomerDaysListActivity : AppCompatActivity() {
    var House: AuctionHouse = AuctionHouse()
    lateinit var HouseId: String
    val LoadingFragment = AuctionDaysSpinner()
    val List = CustomerDaysListFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_days_list)
        HouseId = intent.getStringExtra("HouseId") as String
        House.FetchHouseDays(HouseId, ::setHouseDaysOnScreen)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewAuctionDays,LoadingFragment)
            commit()
        }

        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }
    }

    fun setHouseDaysOnScreen() {
        var auctionHouse = AuctionHouse()
        auctionHouse.SetID(HouseId)
        House.Days.forEach {
            if (!it.Status.equals(AuctionDayStatus.Occurred))
                auctionHouse.Days.add(it)
        }
        List.House = auctionHouse
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewAuctionDays,List)
            commit()
        }
    }
}