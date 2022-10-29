package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RatingBar
import android.widget.TextView
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import org.w3c.dom.Text
import java.text.SimpleDateFormat

class HouseInformationActivity : AppCompatActivity() {
    lateinit var HouseId:String
    lateinit var House:AuctionHouse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_information)

        HouseId = intent.getStringExtra("HouseId") as String
        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Auction House Information"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        House.FetchHouseData(HouseId, ::setHouseInfoOnScreen)




    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setHouseInfoOnScreen () {
        findViewById<TextView>(R.id.house_name).setText(House.GetName())
        findViewById<RatingBar>(R.id.house_rating).rating = House.Rating.toFloat()
        findViewById<TextView>(R.id.house_phone).setText(House.GetPhoneNumber())
        findViewById<TextView>(R.id.house_email).setText(House.GetEmail())
        findViewById<TextView>(R.id.house_phone).setText(House.GetPhoneNumber())
        findViewById<TextView>(R.id.house_closes_sales_day).setText(House.NextSalesDay?.let {
            SimpleDateFormat("dd/MM/yyyy")
                .format(it)
        })
        findViewById<TextView>(R.id.house_closes_sale_start_time).setText(House.Days.firstOrNull()
            ?.PrintStartTime() ?: "null")
    }
}