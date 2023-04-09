package com.example.auctionhouseapp.Fragments

import android.content.Context
import android.content.Intent
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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.auctionhouseapp.Activities.HouseActivity
import com.example.auctionhouseapp.Activities.ViewDay
import com.example.auctionhouseapp.AuctionDayStatus
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.firestore.Query

class AuctionDaysListFragment : Fragment() {

    var House = AuctionHouse()
    private lateinit var text_empty_days_list : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_auction_days_list, container, false)
        val ListView = view.findViewById<ListView>(R.id.auction_days_list)
        val Context = activity as HouseActivity
        text_empty_days_list =  view.findViewById<TextView>(R.id.textView_empty_days_list)
        text_empty_days_list.isVisible = false
        if(House.Days.isEmpty())
            text_empty_days_list.isVisible = true

        ListView.adapter = CustomListAdapter(Context,House)

        ListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(Context, ViewDay::class.java)
            intent.putExtra("Day",House.Days[position])
            Context.resultLauncher.launch(intent)
        }

        view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh).setOnRefreshListener {
            FirebaseUtils.houseCollectionRef
                .document(House.GetUID())
                .collection(Constants.SALES_DAY_COLLECTION)
                .orderBy(Constants.DAY_START_DATE,Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {documents->
                    view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh).isRefreshing = false
                    House.Days.clear()
                    for(doc in documents) {
                        val Day = AuctionDays(doc.data)
                        Day.updateStatus()
                        House.Days.add(Day)
                    }
                    updateDaysList(Context,ListView)
                }.addOnFailureListener {
                    Log.i("CustomerDaysList.kt", "Error! failed to refresh days list")
                }
        }

        return view
    }

    private fun updateDaysList(Context:Context,List:ListView) {
        List.adapter = CustomListAdapter(Context, House)
        text_empty_days_list.isVisible = false
        if (House.Days.isEmpty())
            text_empty_days_list.isVisible = true

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
            View.findViewById<TextView>(R.id.textview_numofitems).setText("No' of Listed Items: " + House.Days[position].ListedItems.size.toString())
            View.findViewById<TextView>(R.id.textview_earnings).setText("Total Earnings: " + House.Days[position].TotalEarnings.toString())
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