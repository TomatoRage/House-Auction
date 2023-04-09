package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.User
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat

class HouseInformationActivity : AppCompatActivity() {
    lateinit var House:AuctionHouse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_information)
        House = intent.getSerializableExtra("House")  as AuctionHouse

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        findViewById<Button>(R.id.btn_upcoming_sales).setOnClickListener {
            val intent = Intent(applicationContext, CustomerDaysListActivity::class.java)
            intent.putExtra("HouseId",House.GetUID())
            startActivity(intent)
        }

        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            val intent = Intent(applicationContext, CustomerMainActivity::class.java)
            startActivity(intent)
            finish()
        }


        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        setHouseInfoOnScreen()

        findViewById<SwipeRefreshLayout>(R.id.swiperefresh).setOnRefreshListener {
            FirebaseUtils.houseCollectionRef
                .document(House.GetUID())
                .get()
                .addOnSuccessListener {
                   findViewById<SwipeRefreshLayout>(R.id.swiperefresh).isRefreshing = false
                    val fetchedHouse = AuctionHouse(it.data)
                    House = fetchedHouse
                    setHouseInfoOnScreen()
                }.addOnFailureListener {
                    Log.i("CustomerItemsList.kt", "Error! failed to refresh day")
                }
        }

    }
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(applicationContext, CustomerMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun setHouseInfoOnScreen () {
        findViewById<TextView>(R.id.house_name).text = House.GetName()
        findViewById<RatingBar>(R.id.house_rating).rating = House.Rating.toFloat()
        findViewById<TextView>(R.id.house_phone).text = House.GetPhoneNumber()
        findViewById<TextView>(R.id.house_email).text = House.GetEmail()
        findViewById<TextView>(R.id.house_closes_sales_day).text = House.NextSalesDay?.let {
            SimpleDateFormat("dd/MM/yyyy")
                .format(it)
        }

        if(House.profile_img_url != null)
            Glide.with(this)
                .load(House.profile_img_url)
                .into(findViewById<ImageView>(R.id.img_house))


        findViewById<TextView>(R.id.house_closes_sale_start_time).text = House.NextSalesDay?.let {
            SimpleDateFormat("HH:mm")
                .format(it)
        }
    }
}