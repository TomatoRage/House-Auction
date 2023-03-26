package com.example.auctionhouseapp.Fragments

import android.app.Activity
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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.auctionhouseapp.Activities.*
import com.example.auctionhouseapp.AuctionDayStatus
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils

class CustomerDaysListFragment : Fragment() {

    var House = AuctionHouse()
    private lateinit var ListView: ListView
    private lateinit var Context: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_auction_days_list, container, false)
        ListView = view.findViewById<ListView>(R.id.auction_days_list)
        Context = activity as CustomerDaysListActivity
        ListView.adapter = CustomListAdapter(Context,House)
        ListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(Context, ItemsList::class.java)
            intent.putExtra("DayId",House.Days[position].DocumentID)
            intent.putExtra("HouseId", House.GetUID())
            val userType = UserType.Customer.ordinal
            intent.putExtra("Type", userType)
            startActivity(intent)
        }

        view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh).setOnRefreshListener {
            FirebaseUtils.houseCollectionRef
                .document(House.GetUID())
                .collection(Constants.SALES_DAY_COLLECTION)
                .get()
                .addOnSuccessListener {documents->
                    view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh).isRefreshing = false
                    House.Days.clear()
                    for(doc in documents) {
                        val Day = AuctionDays(doc.data)
                        Day.updateStatus()
                        if (!Day.Status.equals(AuctionDayStatus.Occurred))
                            House.Days.add(Day)
                    }
                    updateDaysList()
                }.addOnFailureListener {
                    Log.i("CustomerDaysList.kt", "Error! failed to refresh days list")
                }
        }
        return view
    }

    private fun updateDaysList() {
        ListView.adapter = CustomListAdapter(Context,House)
    }

    companion object {
        private val TAG = "CustomerDaysList"
    }

    private class CustomListAdapter(context: Context,house: AuctionHouse): BaseAdapter() {

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
            //View.findViewById<TextView>(R.id.textview_commission).setText((House.Days[position].Commission*100).toInt().toString() + "%")
            View.findViewById<TextView>(R.id.textView_sales_commission).isVisible = false
            View.findViewById<TextView>(R.id.textview_commission).isVisible = false
            View.findViewById<TextView>(R.id.textview_numofitems).setText("No' of Listed Items: " + House.Days[position].ListedItems.size.toString())
            View.findViewById<TextView>(R.id.textview_earnings).isVisible = false
            //View.findViewById<TextView>(R.id.textview_earnings).setText("Total Earnings: " + House.Days[position].Earnings.toString())
            View.findViewById<TextView>(R.id.textview_numofpeople).setText("No' of Participants: " + House.Days[position].ParticipantsNum.toString())
            val param  =  View.findViewById<TextView>(R.id.textview_numofpeople).getLayoutParams() as ViewGroup.MarginLayoutParams
            param.setMargins(param.leftMargin+140,param.topMargin, param.rightMargin, param.bottomMargin)
           val param1  =  View.findViewById<LinearLayout>(R.id.linear_layout_start_time).getLayoutParams() as ViewGroup.MarginLayoutParams
            param1.setMargins(param1.leftMargin+140,param1.topMargin, param1.rightMargin, param1.bottomMargin)
            val param2  =  View.findViewById<ImageView>(R.id.imageView2).getLayoutParams() as ViewGroup.MarginLayoutParams
            param2.setMargins(param2.leftMargin+140,param2.topMargin, param2.rightMargin, param2.bottomMargin)
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