package com.example.auctionhouseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CustomerActivity : AppCompatActivity() {
    val customer:Customer = Customer()
    val list = AuctionHousesListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    fun PerformAfterData() {
//
//        list.customer = customer
//
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.fragmentContainerView,list)
//            commit()
//        }
//
//    }
}