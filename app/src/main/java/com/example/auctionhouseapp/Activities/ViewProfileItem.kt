package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.google.firebase.auth.FirebaseAuth

class ViewProfileItem : AppCompatActivity() {

    private lateinit var item: Item
    private lateinit var txt_item_name:TextView
    private lateinit var txt_item_description:TextView
    private lateinit var txt_item_start_price:TextView
    private lateinit var txt_item_status:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile_item)

        txt_item_name = findViewById<TextView>(R.id.txt_item_name)
        txt_item_description = findViewById<TextView>(R.id.txt_item_description)
        txt_item_start_price = findViewById<TextView>(R.id.txt_start_price)
        txt_item_status = findViewById<TextView>(R.id.txt_item_status)

        item = intent.getSerializableExtra("Item") as Item
        txt_item_name.setText(item.Name)
        txt_item_description.setText(item.Description)
        txt_item_start_price.setText(item.startingPrice.toString())
        txt_item_status.setText(item.status)


        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<ImageView>(R.id.ic_back).setOnClickListener {
            finish()
        }

    }
}