package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.User
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat

class HouseInformationActivity : AppCompatActivity() {
    lateinit var House:AuctionHouse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_information)
        House = intent.getSerializableExtra("House")  as AuctionHouse

        findViewById<Button>(R.id.btn_upcoming_sales).setOnClickListener {
            val intent = Intent(applicationContext, CustomerDaysListActivity::class.java)
            intent.putExtra("House",House)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        setHouseInfoOnScreen()

    }

    fun setHouseInfoOnScreen () {
        findViewById<TextView>(R.id.house_name).text = House.GetName()
        findViewById<RatingBar>(R.id.house_rating).rating = House.Rating.toFloat()
        findViewById<TextView>(R.id.house_phone).text = House.GetPhoneNumber()
        findViewById<TextView>(R.id.house_email).text = House.GetEmail()
        findViewById<TextView>(R.id.house_closes_sales_day).text = House.NextSalesDay?.let {
            SimpleDateFormat("dd/MM/yyyy")
                .format(it)
        }




        findViewById<TextView>(R.id.house_closes_sale_start_time).text = House.NextSalesDay?.let {
            SimpleDateFormat("HH:mm")
                .format(it)
        }
    }
}