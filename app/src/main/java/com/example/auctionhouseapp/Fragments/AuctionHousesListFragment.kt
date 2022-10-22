package com.example.auctionhouseapp.Fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.Activities.CustomerActivity
import com.example.auctionhouseapp.Activities.HouseActivity
import com.example.auctionhouseapp.AuctionDayStatus
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import java.text.SimpleDateFormat


class AuctionHousesListFragment : Fragment() {

    private lateinit var listview : ListView
    var HousesList: ArrayList<AuctionHouse> = arrayListOf()
    //private lateinit var adapterAuctionHousesList: AdapterAuctionHousesList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auction_houses_list, container, false)
        val ListView = view.findViewById<ListView>(R.id.auction_houses_list)
        val Context = activity as CustomerActivity
        ListView.adapter = CustomListAdapter2(Context,HousesList)
        return view
    }

    private class CustomListAdapter2(context: Context, HousesList: ArrayList<AuctionHouse>): BaseAdapter(){

        private val mContext: Context
        private lateinit var mHousesList: ArrayList<AuctionHouse>

        init{
            mContext = context
            mHousesList = HousesList
        }

        override fun getCount(): Int {
            return mHousesList.size
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
            val View = layoutInflater.inflate(R.layout.houses_list_item,parent,false)

            View.findViewById<RatingBar>(R.id.house_rating).rating = mHousesList[position].Rating.toFloat()
            View.findViewById<TextView>(R.id.closest_sale_day).setText(SimpleDateFormat("dd/MM/yyyy")
                .format(mHousesList[position].NextSalesDay))
            Log.i("AuctionHouseListFrag",mHousesList[position].NextSalesDay.toString())
            Log.i("AuctionHouseListFrag",mHousesList[position].Rating.toString())
            Log.i("AuctionHouseListFrag",mHousesList[position].TotalRaters.toString())
            return View
        }
    }

}


