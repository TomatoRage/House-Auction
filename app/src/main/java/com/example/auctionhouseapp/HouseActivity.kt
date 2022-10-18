package com.example.auctionhouseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HouseActivity : AppCompatActivity() {

    val House:AuctionHouse = AuctionHouse()
    val LoadingFragment = AuctionDaysSpinner()
    val List = AuctionDaysListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auctionhouse)

        House.FetchHouseData(Firebase.auth.currentUser!!.uid,::PerformAfterData)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,LoadingFragment)
            commit()
        }

        findViewById<Button>(R.id.btn_add_day).setOnClickListener {
            val intent = Intent(applicationContext, CreateDayActivity::class.java)
            intent.putExtra("House", House.GetUID())
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btn_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun PerformAfterData() {

        List.House = House

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,List)
            commit()
        }

    }
}