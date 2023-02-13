package com.example.auctionhouseapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            replace(R.id.fragmentContainerView3,LoadingFragment)
            commit()
        }

    }

    fun afterItemsFetch() {
        val customerProfileItemsListFragmentList = CustomerProfileItemsListFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView5, customerProfileItemsListFragmentList)
            commit()
        }
    }
}