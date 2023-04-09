package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.auctionhouseapp.R
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.CustomerItemsListFragment
import com.example.auctionhouseapp.Fragments.HouseItemsList
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.Objects.ImagesSharedPref
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils


class ItemsList : AppCompatActivity() {

    var isRequestedList = false
    lateinit var Day:AuctionDays
    lateinit var userType:UserType
    lateinit var DayId:String
    lateinit var HouseId:String
    val LoadingFragment = AuctionDaysSpinner()

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_list)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView3, LoadingFragment)
            commit()
        }

        userType = UserType.getByValue(intent.getIntExtra("Type",0))
        DayId = intent.getStringExtra("DayId") as String
        HouseId = intent.getStringExtra("HouseId") as String

        FirebaseUtils.houseCollectionRef.document(HouseId)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayId)
            .get()
            .addOnSuccessListener {
                Day = AuctionDays(it.data)
                afterDayFetched()
            }.addOnFailureListener {
                Log.i("ItemsList.kt", "Failed while fetching day $DayId")
            }


        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView3,LoadingFragment)
            commit()
        }

    }

    fun afterDayFetched() {
        if(userType == UserType.AuctionHouse) {
            isRequestedList = intent.getBooleanExtra("ListType",false)
            if (isRequestedList)
                Day.FetchRequestedItems(HouseId, ::AfterDataFetch)
            else
                Day.FetchListedItems(HouseId, ::AfterDataFetch, UserType.AuctionHouse)


        } else {
            Day.FetchListedItems(HouseId, ::AfterDataFetch)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun AfterDataFetch(){

        if(supportFragmentManager.isDestroyed)
            return

        if(userType == UserType.Customer) {
            val CustomerList = CustomerItemsListFragment()
            CustomerList.day = Day
            CustomerList.HouseId = HouseId
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView3, CustomerList)
                commit()
            }
        } else {
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