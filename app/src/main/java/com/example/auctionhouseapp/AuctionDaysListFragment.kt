package com.example.auctionhouseapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuctionDaysListFragment : Fragment() {

    var House = AuctionHouse()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auction_days_list, container, false)
        val ListView = view.findViewById<ListView>(R.id.auction_days_list)

        ListView.adapter = CustomListAdapter(activity as HouseActivity,House)

        return view
    }

    companion object {
        private val TAG = "AuctionDaysList"
    }

    private class CustomListAdapter(context: Context,house:AuctionHouse): BaseAdapter(){

        private val mContext: Context
        private var House:AuctionHouse

        init{
            mContext = context
            House = house
        }

        override fun getCount(): Int {
           return House.Days.size
        }

        override fun getItemId(position: Int): Long {
           return position.toLong()
        }

        override fun getItem(position: Int): Any {
            TODO("Not yet implemented")
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val View = layoutInflater.inflate(R.layout.days_list_item,parent,false)

            View.findViewById<TextView>(R.id.textview_title).setText(House.Days[position].Title)
            View.findViewById<TextView>(R.id.textview_date).setText(House.Days[position].PrintDate())
            View.findViewById<TextView>(R.id.textview_time).setText(House.Days[position].PrintStartTime())
            View.findViewById<TextView>(R.id.textview_commission).setText((House.Days[position].Commission*100).toInt().toString() + "%")
            View.findViewById<TextView>(R.id.textview_numofitems).setText("No' of Listed Items: " + House.Days[position].NumOfItems.toString())
            View.findViewById<TextView>(R.id.textview_earnings).setText("Total Earnings: " + House.Days[position].Earnings.toString())
            View.findViewById<TextView>(R.id.textview_numofpeople).setText("No' of Participants: " + House.Days[position].ParticipantsNum.toString())

            return View
        }
    }

}