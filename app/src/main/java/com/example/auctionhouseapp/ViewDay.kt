package com.example.auctionhouseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.Utils.Constants
import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.HashMap

class ViewDay : AppCompatActivity() {

    lateinit var AuctionDay:AuctionDays
    var Title:String = String()
    var Date:Date = Date()
    var Commision:Double = 0.0
    var LockTime:Long = -1
    var Participation:Long = -1
    var Earnings:Long = -1
    var Items:Long = -1
    var Requested:Long = -1
    var Sold:Long = -1

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_day)

        Title = intent.getStringExtra("Day Title")!!
        Date.time = intent.getLongExtra("Start Date",0)
        Commision = intent.getDoubleExtra("Commission", 0.0)
        LockTime = intent.getLongExtra("Lock Time",0)
        Participation = intent.getLongExtra("Participation",0)
        Earnings = intent.getLongExtra("Earnings",0)
        Items = intent.getLongExtra("Items",0)
        Requested = intent.getLongExtra("Requested",0)
        Sold = intent.getLongExtra("Sold",0)

        AuctionDay = AuctionDays( hashMapOf<String,Any>(
            Constants.DAY_NAME to Title,
            Constants.DAY_START_DATE to Timestamp(Date),
            Constants.DAY_COMMISSION to Commision,
            Constants.DAY_LOCK_TIME to LockTime,
            Constants.DAY_NUM_OF_PARTICIPANTS to Participation,
            Constants.DAY_EARNINGS to Earnings,
            Constants.DAY_NUM_OF_ITEMS to Items,
            Constants.DAY_NUM_OF_REQUESTED to Requested,
            Constants.DAY_NUM_OF_SOLD to Sold))
    }
}