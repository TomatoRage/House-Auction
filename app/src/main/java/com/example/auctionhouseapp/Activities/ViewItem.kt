package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Fragments.CustomerItemsListFragment
import com.example.auctionhouseapp.Fragments.HouseItemsList
import com.example.auctionhouseapp.Fragments.ItemInfoFragment
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.FirebaseUtils
import java.text.SimpleDateFormat

class ViewItem : AppCompatActivity() {
    lateinit var HouseId:String
    lateinit var item: Item
    lateinit var day: AuctionDays
    lateinit var userType:UserType
    lateinit var SalesDate:String
    lateinit var StartTime:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)

        item = intent.getSerializableExtra("Item") as Item
        HouseId = intent.getStringExtra("House ID") as String
        userType = UserType.getByValue(intent.getIntExtra("Type",0))
        SalesDate = intent.getStringExtra("SalesDate")!!
        StartTime = intent.getStringExtra("StartTime")!!


        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        setItemInfoOnScreen ()
    }

    fun setItemInfoOnScreen () {
        if(supportFragmentManager.isDestroyed)
            return

        findViewById<ImageView>(R.id.img_item).setImageBitmap(BitmapFactory.decodeByteArray(item.ImagesArray[0],0,item.ImagesArray[0].size))
        findViewById<TextView>(R.id.item_description).text = item.Description

        if(userType == UserType.Customer){
            val info = ItemInfoFragment()
            info.SalesDate = SalesDate
            info.StartTime = StartTime
            info.StartPrice = item.startingPrice
            info.userType = userType
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerViewItemInfo, info)
                commit()
            }
        }else{
            /*TODO IN AUCTION HOUSE MODE*/
        }

    }
}