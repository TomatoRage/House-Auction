package com.example.auctionhouseapp.Activities

import androidx.appcompat.app.AppCompatActivity
import com.example.auctionhouseapp.R
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.UserType

class ItemsList : AppCompatActivity() {

    lateinit var userType:UserType
    lateinit var Day:AuctionDays
    val LoadingFragment = AuctionDaysSpinner()

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_list)

        userType = UserType.getByValue(intent.getIntExtra("Type",0))
        Day = intent.getSerializableExtra("Day",AuctionDays::class.java)!!

        Day.FetchItems(5,::AfterDataFetch)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,LoadingFragment)
            commit()
        }

    }

    fun AfterDataFetch(){

        if(userType == UserType.AuctionHouse){

        }else{

        }

    }
}