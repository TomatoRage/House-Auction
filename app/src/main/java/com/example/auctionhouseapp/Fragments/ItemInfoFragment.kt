package com.example.auctionhouseapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.auctionhouseapp.Activities.CustomerDaysListActivity
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Activities.ViewDay
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType



class ItemInfoFragment : Fragment() {
    lateinit var SalesDate:String
    lateinit var StartTime:String
    lateinit var item: Item
    lateinit var userType: UserType
    lateinit var HouseId: String
    lateinit var DayId: String
    var isRequestedList = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_item_info, container, false)
        view.findViewById<TextView>(R.id.item_sales_day).setText(SalesDate)
        view.findViewById<TextView>(R.id.item_sales_start_time).setText(StartTime)
        view.findViewById<TextView>(R.id.btn_accept_item).isVisible = false
        view.findViewById<TextView>(R.id.btn_reject_item).isVisible = false
        if (userType == UserType.Customer) {
            view.findViewById<TextView>(R.id.item_start_price).isVisible = false
            view.findViewById<TextView>(R.id.txt_start_price).isVisible = false
        } else {
            view.findViewById<TextView>(R.id.item_start_price).setText(item.startingPrice.toString())
            if (isRequestedList) {
                view.findViewById<TextView>(R.id.btn_accept_item).isVisible = true
                view.findViewById<TextView>(R.id.btn_reject_item).isVisible = true
                view.findViewById<TextView>(R.id.btn_accept_item).setOnClickListener {
                    item.StoreDataIntoItemsList(HouseId,DayId,::deleteItemFromReqItemsList)
                    Toast.makeText(context, "Item Accepted!", Toast.LENGTH_SHORT).show()
                }
                view.findViewById<TextView>(R.id.btn_reject_item).setOnClickListener {
                    item.DeleteFromRequestedList(HouseId,DayId,::backToPrevActivity)
                    Toast.makeText(context, "Item Rejected!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    fun deleteItemFromReqItemsList() {
        item.DeleteFromRequestedList(HouseId,DayId,::backToPrevActivity)

    }

    fun backToPrevActivity() {
        val intent = Intent(context, ViewDay::class.java)
        intent.putExtra("Day",DayId)
        startActivity(intent)
    }
}