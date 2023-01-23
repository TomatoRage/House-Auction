package com.example.auctionhouseapp.Fragments

import android.content.Context
import android.content.Intent
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
import com.example.auctionhouseapp.Activities.CustomerMainActivity
import com.example.auctionhouseapp.Activities.HouseInformationActivity
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import java.text.SimpleDateFormat


class AuctionHousesListFragment : Fragment() {

    var HousesList: ArrayList<AuctionHouse> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        val view = inflater.inflate(R.layout.fragment_auction_houses_list, container, false)
        val ListView = view.findViewById<ListView>(R.id.auction_houses_list)
        val Context = activity as CustomerMainActivity
        ListView.adapter = CustomListAdapter2(Context,HousesList)
        ListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(Context, HouseInformationActivity::class.java)
            intent.putExtra("HouseId",HousesList[position].GetUID())
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        return view
    }

    private class CustomListAdapter2(context: Context, HousesList: ArrayList<AuctionHouse>): BaseAdapter(){

        private val mContext: Context
        private var mHousesList: ArrayList<AuctionHouse>

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
            View.findViewById<TextView>(R.id.house_title).setText(mHousesList[position].GetName())
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


