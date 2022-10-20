package com.example.auctionhouseapp.Activities

import androidx.appcompat.app.AppCompatActivity
import com.example.auctionhouseapp.R
import android.os.Bundle
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.UserType

class ItemsList : AppCompatActivity() {

    lateinit var userType:UserType
    lateinit var Items:ArrayList<Item>
    val LoadingFragment = AuctionDaysSpinner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_list)

        userType = UserType.getByValue(intent.getIntExtra("Type",0))

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,LoadingFragment)
            commit()

            if(userType == UserType.AuctionHouse){
                //TODO: Fill in User List
            }else{

            }
        }

    }
}