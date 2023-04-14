package com.example.auctionhouseapp.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Activities.*
import com.example.auctionhouseapp.AuctionDayStatus
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class CustomerItemsListFragment : Fragment() {

    var day = AuctionDays()
    lateinit var HouseId:String
    private lateinit var text_empty_items_list : TextView
    private lateinit var ListView: ListView
    private lateinit var Context:Activity
    private lateinit var DayId:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_items_list, container, false)
        text_empty_items_list =  view.findViewById<TextView>(R.id.textView_empty_items_list)
        text_empty_items_list.isVisible = false
        if(day.ListedItems.isEmpty())
            text_empty_items_list.isVisible = true
        DayId = day.DocumentID
        Context = activity as ItemsList
        ListView = view.findViewById<ListView>(R.id.auction_house_items)
        ListView.adapter = CustomListAdapter(Context,day.ListedItems)
        ListView.setOnItemClickListener { parent, view, position, id ->
            if(!day.ListedItems.isEmpty()) {
                val intent = Intent(Context, ViewItem::class.java)
                val item = day.ListedItems[position]
                intent.putExtra("Item", item)
                intent.putExtra("SalesDate", day.PrintDate())
                intent.putExtra("StartTime", day.PrintStartTime())
                intent.putExtra("HouseID", HouseId)
                intent.putExtra("DayID", DayId)
                intent.putExtra("Commission", day.Commission)
                val userType = UserType.Customer.ordinal
                intent.putExtra("Type", userType)
                startActivity(intent)
                Context.finish()
            }
        }

        view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh).setOnRefreshListener {
            FirebaseUtils.houseCollectionRef
                .document(HouseId)
                .collection(Constants.SALES_DAY_COLLECTION)
                .document(DayId)
                .get()
                .addOnSuccessListener {
                    view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh).isRefreshing = false
                    day = AuctionDays(it.data)
                    day.FetchListedItems(HouseId,::updateListView)
                }.addOnFailureListener {
                    Log.i("CustomerItemsList.kt", "Error! failed to refresh day")
                }
        }

        view.findViewById<TextView>(R.id.txt_back).setOnClickListener {
            val intent = Intent(context, CustomerDaysListActivity::class.java)
            intent.putExtra("HouseId", HouseId)
            startActivity(intent)
            Context.finish()
        }

        view.findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(Context, LoginActivity::class.java)
            startActivity(intent)
        }


        view.findViewById<Button>(R.id.btn_auction_item).setOnClickListener {

            val Currentdate:LocalDateTime = LocalDateTime.now()
            val currentDate:Calendar = Calendar.getInstance()

            currentDate.set(Calendar.YEAR, Currentdate.year)
            currentDate.set(Calendar.MONTH, Currentdate.monthValue)
            currentDate.set(Calendar.DAY_OF_MONTH, Currentdate.dayOfMonth)
            currentDate.set(Calendar.HOUR_OF_DAY, Currentdate.hour)
            currentDate.set(Calendar.MINUTE, Currentdate.minute)


            val LockDate:Calendar = Calendar.getInstance()
            LockDate.setTime(day.StartDate)
            LockDate.setTimeZone(TimeZone.getTimeZone("Israel"))
            LockDate.add(Calendar.MONTH,1)
            LockDate.add(Calendar.HOUR_OF_DAY,-1*day.LockBefore)

            if (currentDate.after(LockDate)) {
                Toast.makeText(Context, "Auctions Have Been Locked!", Toast.LENGTH_SHORT).show()

            } else {
                val intent = Intent(Context, AuctionItemActivity::class.java)
                intent.putExtra("Day ID",day.DocumentID)
                intent.putExtra("House ID", HouseId)
                intent.putExtra("Commission", day.Commission)
                //intent.putExtra("Type", UserType.Customer.Type)
                startActivity(intent)
            }


        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(context, CustomerDaysListActivity::class.java)
                intent.putExtra("HouseId", HouseId)
                startActivity(intent)
                Context.finish()
            }
        }

        (activity as ItemsList).onBackPressedDispatcher.addCallback((activity as ItemsList),onBackPressedCallback)
        return view
    }





    private fun checkCustomerCashSufficiency(startingPrice: Int, intent: Intent) {
        val currentCustomer = FirebaseAuth.getInstance().uid.toString()
        FirebaseUtils.customerCollectionRef
            .document(currentCustomer)
            .get()
            .addOnSuccessListener {
                val customer = Customer(it.data)
                val customerCash = customer.getCash()
                if (customerCash >= startingPrice || !day.Status.equals(AuctionDayStatus.Happening)) {
                    startActivity(intent)
                    Context.finish()
                } else {
                    Toast.makeText(activity, "Blocked !\nNot Enough Cash In Your Account To Bid For This Item", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Failed to update winner name")
            }

    }

    private fun updateListView() {
        ListView.adapter = CustomListAdapter(Context,day.ListedItems)
        if(!day.ListedItems.isEmpty())
            text_empty_items_list.isVisible = false
    }
    private class CustomListAdapter(context: Context,items:ArrayList<Item>): BaseAdapter(){

        private val mContext:Context
        private val Items:ArrayList<Item>

        init{
            mContext = context
            Items = items
        }

        override fun getCount(): Int {
            return Items.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val View = layoutInflater.inflate(R.layout.house_item_list_item,parent,false)
            View.findViewById<TextView>(R.id.textview_house_item_name).setText(Items[position]._name)
            View.findViewById<TextView>(R.id.textView_description).setText(Items[position]._description)
            Glide.with(mContext)
                .load(Items[position]._imagesUrls.get(0))
                .into(View.findViewById<ImageView>(R.id.imageView_house_item))
            View.findViewById<ImageView>(R.id.imageView_house_item).setBackgroundResource(R.drawable.round_outline)
            View.findViewById<ImageView>(R.id.imageView_house_item).clipToOutline = true

            return View
        }
    }

}