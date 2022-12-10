package com.example.auctionhouseapp.Fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.auctionhouseapp.Activities.AuctionItemActivity
import com.example.auctionhouseapp.Activities.ItemsList
import com.example.auctionhouseapp.Activities.LoginActivity
import com.example.auctionhouseapp.Activities.ViewItem
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.example.auctionhouseapp.Objects.Item
import java.text.SimpleDateFormat


class ItemInfoFragment : Fragment() {
    lateinit var SalesDate:String
    lateinit var StartTime:String
    var StartPrice = 0
    lateinit var userType: UserType
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_item_info, container, false)
        view.findViewById<TextView>(R.id.item_sales_day).setText(SalesDate)
        view.findViewById<TextView>(R.id.item_sales_start_time).setText(StartTime)
        if (userType == UserType.Customer) {
            view.findViewById<TextView>(R.id.item_start_price).isVisible = false
            view.findViewById<TextView>(R.id.txt_start_price).isVisible = false
        } else {
            view.findViewById<TextView>(R.id.item_start_price).setText(StartPrice)
        }
        return view
    }
}