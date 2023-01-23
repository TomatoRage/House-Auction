package com.example.auctionhouseapp.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.auctionhouseapp.R
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.AuctionHousesListFragment
import com.example.auctionhouseapp.Fragments.CustomerItemsListFragment
import com.example.auctionhouseapp.Fragments.HouseItemsList
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Extensions.toast

class ItemsList : AppCompatActivity() {

    var isRequestedList = false
    lateinit var userType:UserType
    lateinit var Day:AuctionDays
    lateinit var HouseID:String
    val LoadingFragment = AuctionDaysSpinner()
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_list)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView3, LoadingFragment)
            commit()
        }

        userType = UserType.getByValue(intent.getIntExtra("Type",0))
        Day = intent.getSerializableExtra("Day") as AuctionDays
        HouseID = intent.getStringExtra("House ID")!!

        if(userType == UserType.AuctionHouse) {
            isRequestedList = intent.getBooleanExtra("ListType",false)
            if (isRequestedList)
                Day.FetchRequested(5, HouseID, ::AfterDataFetch)
            else
                Day.FetchItems(5, HouseID, ::AfterDataFetch)


        } else {
            Day.FetchItems(5, HouseID, ::AfterDataFetch)
        }



        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView3,LoadingFragment)
            commit()
        }
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainerView3,LoadingFragment)
                    commit()
                }
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()

    }

    fun AfterDataFetch(){

        if(supportFragmentManager.isDestroyed)
            return

        if(userType == UserType.Customer){
            val CustomerList = CustomerItemsListFragment()
            CustomerList.day = Day
            CustomerList.HouseId = HouseID
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView3, CustomerList)
                commit()
            }
        } else{
            val HouseList = HouseItemsList()
            HouseList.Day = Day
            HouseList.isRequestedList = isRequestedList
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView3, HouseList)
                commit()
            }
        }

    }
}