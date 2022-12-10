package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Fragments.ItemInfoFragment
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils


class ViewItem : AppCompatActivity() {
    lateinit var HouseId:String
    lateinit var item: Item
    lateinit var day: AuctionDays
    lateinit var userType:UserType
    lateinit var SalesDate:String
    lateinit var StartTime:String
    lateinit var imageSwitcher: ImageSwitcher
    lateinit var NextBtn: ImageButton
    lateinit var PrevBtn: ImageButton
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)
        imageSwitcher = findViewById<ImageSwitcher>(R.id.img_item)
        imageSwitcher.setFactory { ImageView(applicationContext) }
        item = intent.getSerializableExtra("Item") as Item
        HouseId = intent.getStringExtra("House ID") as String
        userType = UserType.getByValue(intent.getIntExtra("Type",0))
        SalesDate = intent.getStringExtra("SalesDate")!!
        StartTime = intent.getStringExtra("StartTime")!!
        NextBtn = findViewById<ImageButton>(R.id.btn_next_img)
        PrevBtn = findViewById<ImageButton>(R.id.btn_prev_img)

        item.FetchImages(-1,::setItemInfoOnScreen)
        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        NextBtn.setOnClickListener { 
            if (position < item.ImagesArray.size - 1) {
                position++
                val bitmap = BitmapDrawable(BitmapFactory.decodeByteArray(item.ImagesArray[position],0,item.ImagesArray[position].size))
                imageSwitcher.setImageDrawable(bitmap)
                
            } else {
                toast("No More Images...")
            }
        }
        
        PrevBtn.setOnClickListener { 
            if (position > 0) {
                position--
                val bitmap = BitmapDrawable(BitmapFactory.decodeByteArray(item.ImagesArray[position],0,item.ImagesArray[position].size))
                imageSwitcher.setImageDrawable(bitmap)
            } else {
                toast("No More Images...")
            }
        }
    }

    fun setItemInfoOnScreen () {
        if(supportFragmentManager.isDestroyed)
            return

        val bitmap = BitmapDrawable(BitmapFactory.decodeByteArray(item.ImagesArray[0],0,item.ImagesArray[0].size))
        imageSwitcher.setImageDrawable(bitmap)
        findViewById<TextView>(R.id.item_description).text = item.Description

        if(userType == UserType.Customer) {
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