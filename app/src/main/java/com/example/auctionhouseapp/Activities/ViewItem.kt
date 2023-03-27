package com.example.auctionhouseapp.Activities

import com.example.auctionhouseapp.R
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.AuctionHouseViewItemFragment
import com.example.auctionhouseapp.Fragments.CustomerViewItemFragment
import com.example.auctionhouseapp.Fragments.ItemViewBidFragment
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class ViewItem : AppCompatActivity() {
    lateinit var item: Item
    lateinit var userType:UserType
    lateinit var SalesDate:String
    lateinit var StartTime:String
    lateinit var imageView: ImageView
    private var isRequestedList = false
    private lateinit var HouseId: String
    private lateinit var DayId: String
    private var Commission:Double = 0.1
    private lateinit var AuctionDate:Date
    val LoadingFragment = AuctionDaysSpinner()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item)
        //imageSwitcher = findViewById<ImageSwitcher>(R.id.img_item)


        item = intent.getSerializableExtra("Item") as Item
        userType = UserType.getByValue(intent.getIntExtra("Type",0))
        SalesDate = intent.getStringExtra("SalesDate")!!
        StartTime = intent.getStringExtra("StartTime")!!
        HouseId = intent.getStringExtra("HouseID")!!
        DayId = intent.getStringExtra("DayID")!!
        Commission = intent.getDoubleExtra("Commission",Commission)
        isRequestedList = intent.getBooleanExtra("ListType", false)


        val date = SalesDate.split("/")
        val clock = StartTime.split(":")
        val AuctionTime:Calendar = Calendar.getInstance()
        AuctionTime.set(Calendar.YEAR, date.get(2).toInt())
        AuctionTime.set(Calendar.MONTH, date.get(1).toInt()-1)
        AuctionTime.set(Calendar.DAY_OF_MONTH, date.get(0).toInt())
        AuctionTime.set(Calendar.HOUR_OF_DAY, clock.get(0).toInt())
        AuctionTime.set(Calendar.MINUTE, clock.get(1).toInt())
        AuctionDate = AuctionTime.time

        FirebaseUtils.itemsCollectionRef
            .document(item._id)
            .get()
            .addOnSuccessListener {
                val fetchedItem = Item(it.data)
                item._last_bid_time = fetchedItem._last_bid_time
                viewItemFragment()
            } .addOnFailureListener {
                Log.i("ViewItem.kt","Warning !! Failed to fetch updated time of the last bid")
            }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewItemInfo, LoadingFragment)
            commit()
        }

        //item.FetchImages(-1,::setItemInfoOnScreen)
        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        if(supportFragmentManager.isDestroyed)
            return
    }

    private fun viewItemFragment() {
        val TimeNow = Timestamp(Date()).toDate()
        if(AuctionDate.after(TimeNow)) {
            if (userType == UserType.Customer) {
                val itemInfo = CustomerViewItemFragment()
                itemInfo.item = item
                itemInfo.SalesDate = SalesDate
                itemInfo.StartTime = StartTime
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainerViewItemInfo, itemInfo)
                    commit()
                }
            } else {
                val itemInfo = AuctionHouseViewItemFragment()
                itemInfo.item = item
                itemInfo.SalesDate = SalesDate
                itemInfo.StartTime = StartTime
                itemInfo.HouseId = HouseId
                itemInfo.DayId = DayId
                itemInfo.isRequestedList = isRequestedList
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainerViewItemInfo, itemInfo)
                    commit()
                }

            }
        } else {
            val itemInfo = ItemViewBidFragment()
            itemInfo.item = item
            itemInfo.Commission = Commission
            itemInfo.HouseId = HouseId
            itemInfo.DayId = DayId
            itemInfo.userType = userType
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerViewItemInfo, itemInfo)
                commit()
            }
        }

    }
}
