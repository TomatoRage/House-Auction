package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import java.text.SimpleDateFormat

class HouseInformationActivity : AppCompatActivity() {
    lateinit var HouseId:String
    lateinit var House:AuctionHouse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_information)
        House = AuctionHouse()

        HouseId = intent.getStringExtra("HouseId") as String
        House.FetchHouseData(HouseId, ::setHouseInfoOnScreen)
        findViewById<Button>(R.id.btn_upcoming_sales).setOnClickListener{
            val intent = Intent(applicationContext, CustomerDaysListActivity::class.java)
            intent.putExtra("AucHouseId",HouseId)
            startActivity(intent)
        }

    }

    fun setHouseInfoOnScreen () {
        findViewById<TextView>(R.id.house_name).text = House.GetName()
        findViewById<RatingBar>(R.id.house_rating).rating = House.Rating.toFloat()
        findViewById<TextView>(R.id.house_phone).text = House.GetPhoneNumber()
        findViewById<TextView>(R.id.house_email).text = House.GetEmail()
        findViewById<TextView>(R.id.house_phone).text = House.GetPhoneNumber()
        findViewById<TextView>(R.id.house_closes_sales_day).text = House.NextSalesDay?.let {
            SimpleDateFormat("dd/MM/yyyy")
                .format(it)
        }
        findViewById<TextView>(R.id.house_closes_sale_start_time).text = House.Days.firstOrNull()
            ?.PrintStartTime() ?: "null"
    }
}