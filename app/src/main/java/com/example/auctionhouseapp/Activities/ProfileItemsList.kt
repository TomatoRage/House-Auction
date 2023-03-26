package com.example.auctionhouseapp.Activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.CustomerProfileItemsListFragment
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.google.firebase.auth.FirebaseAuth

class ProfileItemsList : AppCompatActivity() {

    var customer:Customer = Customer()
    val customerUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
    var item:ArrayList<Item> = arrayListOf()
    lateinit var itemType:String
    val LoadingFragment = AuctionDaysSpinner()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_items_list)
        itemType = intent.getStringExtra("Items Type") as String
        if (itemType == "Auctioned") {
            customer.fetchCustomerAuctionedItems(customerUID,::afterItemsFetch)
        }
        else if (itemType == "Bidded") {
            customer.fetchCustomerBiddedItems(customerUID,::afterItemsFetch)
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView5,LoadingFragment)
            commit()
        }
    }


    fun afterItemsFetch() {
        var customerProfileItemsListFragmentList = CustomerProfileItemsListFragment()
        supportFragmentManager.beginTransaction().apply {
            if (itemType == "Auctioned") {
                customerProfileItemsListFragmentList.items = customer.auctionedItems
                customerProfileItemsListFragmentList.type = "Auctioned"
            } else if (itemType == "Bidded") {
                customerProfileItemsListFragmentList.items = customer.biddedItems
                customerProfileItemsListFragmentList.type = "Bidded"
            }
            replace(R.id.fragmentContainerView5, customerProfileItemsListFragmentList)
            commit()
        }
    }

}