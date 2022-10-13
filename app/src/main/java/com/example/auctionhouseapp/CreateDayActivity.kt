package com.example.auctionhouseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CreateDayActivity : AppCompatActivity() {

    lateinit var HouseID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_day)

       HouseID = intent.getStringExtra("House")!!
    }
}