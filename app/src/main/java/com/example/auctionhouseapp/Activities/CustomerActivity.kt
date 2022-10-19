package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.AuctionHousesListFragment
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.FirebaseUtils

class CustomerActivity : AppCompatActivity() {
    //val customer: Customer = Customer()
    val List = AuctionHousesListFragment()
    val LoadingFragment = AuctionDaysSpinner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, LoadingFragment)
            commit()
        }

        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, List)
            commit()
        }
    }
}
