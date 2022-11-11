package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.FirebaseUtils

class CustomerItemsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_items_list)

        findViewById<TextView>(R.id.btn_sign_out_customer_list_items).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btn_back_customer_list_items).setOnClickListener {
            finish()
        }

    }
}