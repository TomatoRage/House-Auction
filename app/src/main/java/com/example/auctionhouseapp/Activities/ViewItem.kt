package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.FirebaseUtils
import java.text.SimpleDateFormat

class ViewItem : AppCompatActivity() {
    lateinit var HouseId:String
    lateinit var item: Item
    lateinit var day: AuctionDays
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)

        HouseId = intent.getStringExtra("HouseId") as String
        day = intent.getStringExtra("Day",) as AuctionDays
        item = intent.getStringExtra("Item") as Item


        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun setHouseInfoOnScreen () {
        findViewById<ImageView>(R.id.img_item).setImageBitmap(item.ImagesArray[0])
        findViewById<TextView>(R.id.item_description).text = item.Description
        findViewById<TextView>(R.id.item_sales_day).setText(
            SimpleDateFormat("dd/MM/yyyy")
                .format(day.StartDate)
        )
        findViewById<TextView>(R.id.item_sales_start_time).setText(
            SimpleDateFormat("HH:mm:ss")
                .format(day.StartDate)
        )

        findViewById<TextView>(R.id.item_start_price).isVisible = false
    }
}