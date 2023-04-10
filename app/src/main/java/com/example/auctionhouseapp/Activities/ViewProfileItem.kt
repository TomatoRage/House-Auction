package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.google.firebase.auth.FirebaseAuth

class ViewProfileItem : AppCompatActivity() {

    private lateinit var item: Item
    private lateinit var type: String
    private lateinit var txt_item_name:TextView
    private lateinit var txt_item_description:TextView
    private lateinit var txt_item_start_price:TextView
    private lateinit var txt_item_status:TextView
    private lateinit var txt_item_owner_phone:TextView
    private lateinit var txt_item_winner_phone:TextView
    private lateinit var txt_item_auction_house:TextView
    private lateinit var txt_sold_for:TextView
    private lateinit var txt_sold_for_price:TextView
    private lateinit var imageView: ImageView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile_item)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        imageView = findViewById<ImageView>(R.id.img_item)
        txt_item_name = findViewById<TextView>(R.id.txt_item_name)
        txt_item_start_price = findViewById<TextView>(R.id.txt_starting_price)
        txt_item_status = findViewById<TextView>(R.id.txt_status)
        txt_item_owner_phone = findViewById<TextView>(R.id.txt_owner_phone)
        txt_item_winner_phone = findViewById<TextView>(R.id.txt_buyer_phone)
        txt_item_auction_house = findViewById<TextView>(R.id.txt_auction_house)
        txt_sold_for = findViewById<TextView>(R.id.txt_sold_for)
        txt_sold_for_price = findViewById<TextView>(R.id.txt_sold_for_price)

        item = intent.getSerializableExtra("Item") as Item
        type = intent.getStringExtra("Items Type") as String


        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<ImageView>(R.id.ic_back).setOnClickListener {
            val intent = Intent(applicationContext, ProfileItemsList::class.java)
            intent.putExtra("Items Type", type)
            startActivity(intent)
            finish()
        }
        setItemInfoOnScreen()

    }

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(applicationContext, ProfileItemsList::class.java)
            intent.putExtra("Items Type", type)
            startActivity(intent)
            finish()
        }
    }

    fun setItemInfoOnScreen() {
        item = intent.getSerializableExtra("Item") as Item
        Glide.with(this)
            .load(item._imagesUrls.get(0))
            .into(imageView)
        //val bitmap = BitmapDrawable(BitmapFactory.decodeByteArray(item.ImagesArray[0],0,item.ImagesArray[0].size))
        //imageSwitcher.setImageDrawable(bitmap)
        txt_item_name.setText(item._name)
        txt_item_start_price.setText(item._startingPrice.toString())
        txt_item_status.setText(item._status)
        txt_item_owner_phone.setText(item._ownerPhoneNumber)
        txt_item_winner_phone.setText(item._winnerPhoneNumber)
        txt_item_auction_house.setText(item._auctionHouseName)
        val currentCustomer = FirebaseAuth.getInstance().uid.toString()
        if (item._lastBid <= 0) {
            txt_sold_for.isVisible = false
            txt_sold_for_price.isVisible = false
        } else {
            if (currentCustomer.equals(item._ownerId)) {
                txt_sold_for.setText("Sold For(Your share):")
                val sold_for_price = item._lastBid * (1 - item._commission)
                txt_sold_for_price.setText(sold_for_price.toString())
            } else {
                txt_sold_for.setText("Bought For:")
                txt_sold_for_price.setText(item._lastBid.toString())
            }
        }

    }
}