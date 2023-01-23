package com.example.auctionhouseapp.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.ItemInfoFragment
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.google.firebase.auth.FirebaseAuth


class ViewItem : AppCompatActivity() {
    lateinit var item: Item
    lateinit var userType:UserType
    lateinit var SalesDate:String
    lateinit var StartTime:String
    lateinit var imageSwitcher: ImageSwitcher
    lateinit var NextBtn: ImageButton
    lateinit var PrevBtn: ImageButton
    lateinit var itemName: TextView
    lateinit var itemDescription: TextView
    lateinit var txtItemDescription: TextView
    private var position = 0
    private var isRequestedList = false
    private lateinit var HouseId: String
    private lateinit var DayId: String
    val LoadingFragment = AuctionDaysSpinner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)
        imageSwitcher = findViewById<ImageSwitcher>(R.id.img_item)
        imageSwitcher.setFactory { ImageView(applicationContext) }
        item = intent.getSerializableExtra("Item") as Item
        userType = UserType.getByValue(intent.getIntExtra("Type",0))
        SalesDate = intent.getStringExtra("SalesDate")!!
        StartTime = intent.getStringExtra("StartTime")!!
        HouseId = intent.getStringExtra("HouseID")!!
        DayId = intent.getStringExtra("DayID")!!
        isRequestedList = intent.getBooleanExtra("ListType",false)
        NextBtn = findViewById<ImageButton>(R.id.btn_next_img)
        PrevBtn = findViewById<ImageButton>(R.id.btn_prev_img)
        itemName = findViewById<TextView>(R.id.item_name)
        itemDescription = findViewById<TextView>(R.id.item_description)
        txtItemDescription =  findViewById<TextView>(R.id.txt_item_description)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewItemInfo, LoadingFragment)
            commit()
        }

        item.FetchImages(-1,::setItemInfoOnScreen)
        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
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
        itemName = itemName
        itemDescription.text = item.Description
        itemDescription.isVisible = true
        txtItemDescription.isVisible = true
        val info = ItemInfoFragment()
        info.SalesDate = SalesDate
        info.StartTime = StartTime
        info.item = item
        info.userType = userType
        info.isRequestedList = isRequestedList
        info.HouseId = HouseId
        info.DayId = DayId
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewItemInfo, info)
            commit()
        }
    }
}