package com.example.auctionhouseapp.Activities

import androidx.appcompat.app.AppCompatActivity
import com.example.auctionhouseapp.R
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.HouseItemsList
import com.example.auctionhouseapp.UserType

class ItemsList : AppCompatActivity() {

    lateinit var userType:UserType
    lateinit var Day:AuctionDays
    lateinit var HouseID:String
    val LoadingFragment = AuctionDaysSpinner()

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_list)

        userType = UserType.getByValue(intent.getIntExtra("Type",0))
        Day = intent.getSerializableExtra("Day",) as AuctionDays
        HouseID = intent.getStringExtra("House ID")!!

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView3,LoadingFragment)
            commit()
        }

        Day.FetchItems(5,HouseID,::AfterDataFetch)

    }

    fun AfterDataFetch(){

        if(userType == UserType.Customer){
            //TODO: Fill in functionality
        }else{
            val HouseList = HouseItemsList()
            HouseList.Day = Day

            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView3, HouseList)
                commit()
            }
        }

    }
}