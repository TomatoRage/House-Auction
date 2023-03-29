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
import androidx.annotation.RequiresApi
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
    private lateinit var txt_item_owner_tel:TextView
    private lateinit var txt_item_winner_tel:TextView
    private lateinit var txt_item_auction_house:TextView
    private lateinit var imageView: ImageView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile_item)

        imageView = findViewById<ImageView>(R.id.img_item)
        //imageSwitcher.setFactory { ImageView(applicationContext) }
        txt_item_name = findViewById<TextView>(R.id.txt_item_name)
        txt_item_description = findViewById<TextView>(R.id.txt_item_description)
        txt_item_start_price = findViewById<TextView>(R.id.txt_start_price)
        txt_item_status = findViewById<TextView>(R.id.txt_item_status)
        txt_item_owner_tel = findViewById<TextView>(R.id.txt_owner_tel)
        txt_item_winner_tel = findViewById<TextView>(R.id.txt_auction_winner_tel)
        txt_item_auction_house = findViewById<TextView>(R.id.txt_item_auction_house)

        item = intent.getSerializableExtra("Item") as Item
        type = intent.getStringExtra("Items Type") as String
        //item.FetchImages(-1,::setItemInfoOnScreen)


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
        }
        setItemInfoOnScreen()

    }

    fun setItemInfoOnScreen() {
        item = intent.getSerializableExtra("Item") as Item
        Glide.with(this)
            .load(item._imagesUrls.get(0))
            .into(imageView)
        //val bitmap = BitmapDrawable(BitmapFactory.decodeByteArray(item.ImagesArray[0],0,item.ImagesArray[0].size))
        //imageSwitcher.setImageDrawable(bitmap)
        txt_item_name.setText(item._name)
        txt_item_description.setText(item._description)
        txt_item_start_price.setText(item._startingPrice.toString())
        txt_item_status.setText(item._status)
        txt_item_owner_tel.setText(item._ownerPhoneNumber)
        txt_item_winner_tel.setText(item._winnerPhoneNumber)
        txt_item_auction_house.setText(item._auctionHouseName)
    }
}