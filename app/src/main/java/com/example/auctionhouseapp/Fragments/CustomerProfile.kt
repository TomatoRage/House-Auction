package com.example.auctionhouseapp.Fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.auctionhouseapp.Activities.ProfileItemsList
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.R
import com.google.firebase.auth.FirebaseAuth


class CustomerProfile : Fragment() {

    var customer:Customer = Customer()
    var customerName: String = String()
    var customerEmail =  String()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val txt_profile_name  = view.findViewById<TextView>(R.id.txt_profile_name)
        val txt_profile_email = view.findViewById<TextView>(R.id.txt_profile_email)
        val my_auction_items  = view.findViewById<CardView>(R.id.my_auctioned_items)
        val my_bidded_items   = view.findViewById<CardView>(R.id.my_bidded_items)

        txt_profile_name.setText(customerName)
        txt_profile_email.setText(customerEmail)

        my_auction_items.setOnClickListener{
           var intent = Intent(activity, ProfileItemsList::class.java)
            intent.putExtra("Items Type", "Auctioned")
            startActivity(intent)
        }

        my_bidded_items.setOnClickListener{
            var intent = Intent(activity,ProfileItemsList::class.java)
            intent.putExtra("Items Type", "Bidded")
            startActivity(intent)
        }

        return view
    }

}