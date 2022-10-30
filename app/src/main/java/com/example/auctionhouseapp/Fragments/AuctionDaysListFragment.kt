package com.example.auctionhouseapp.Fragments

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.example.auctionhouseapp.Activities.HouseActivity
import com.example.auctionhouseapp.Activities.ViewDay
import com.example.auctionhouseapp.AuctionDayStatus
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R

class AuctionDaysListFragment : Fragment() {

    var House = AuctionHouse()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_auction_days_list, container, false)
        val ListView = view.findViewById<ListView>(R.id.auction_days_list)
        val Context = activity as HouseActivity

        ListView.adapter = CustomListAdapter(Context,House)

        ListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(Context, ViewDay::class.java)
            intent.putExtra("Day",House.Days[position])
            Context.resultLauncher.launch(intent)
        }

        return view
    }

    companion object {
        private val TAG = "AuctionDaysList"
    }

    private class CustomListAdapter(context: Context,house: AuctionHouse): BaseAdapter(){

        private val mContext: Context
        private var House: AuctionHouse

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
            return position
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
            if(House.Days[position].Status == AuctionDayStatus.Pending) {
                View.findViewById<View>(R.id.indication_light).backgroundTintList = ColorStateList.valueOf(Color.RED)
                View.findViewById<TextView>(R.id.textview_status).setText("Pending")
                View.findViewById<TextView>(R.id.textview_status).setTextColor(Color.RED)
            }else if(House.Days[position].Status == AuctionDayStatus.Happening) {
                View.findViewById<View>(R.id.indication_light).backgroundTintList = ColorStateList.valueOf(Color.BLUE)
                View.findViewById<TextView>(R.id.textview_status).setText("Happening")
                View.findViewById<TextView>(R.id.textview_status).setTextColor(Color.BLUE)
            }else if(House.Days[position].Status == AuctionDayStatus.Occurred) {
                View.findViewById<View>(R.id.indication_light).backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                View.findViewById<TextView>(R.id.textview_status).setText("Occurred")
                View.findViewById<TextView>(R.id.textview_status).setTextColor(Color.GREEN)
            }


            return View
        }
    }

}