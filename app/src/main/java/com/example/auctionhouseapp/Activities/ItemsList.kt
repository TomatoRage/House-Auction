package com.example.auctionhouseapp.Activities

import androidx.appcompat.app.AppCompatActivity
import com.example.auctionhouseapp.R
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.CustomerItemsListFragment
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

    override fun onDestroy() {
        super.onDestroy()

    }

    fun AfterDataFetch(){

        if(supportFragmentManager.isDestroyed)
            return

        if(userType == UserType.Customer){
            //TODO: Fill in functionality
            val HouseList = CustomerItemsListFragment()
            HouseList.Day = Day
            HouseList.HouseId = HouseID
            findViewById<FragmentContainerView>(R.id.fragmentContainerView3).isVisible=false
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView4, HouseList)
                commit()
            }
        }else{
            val HouseList = CustomerItemsListFragment()
            HouseList.Day = Day
            findViewById<FragmentContainerView>(R.id.fragmentContainerView4).isVisible=false
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView3, HouseList)
                commit()
            }
        }

    }
}