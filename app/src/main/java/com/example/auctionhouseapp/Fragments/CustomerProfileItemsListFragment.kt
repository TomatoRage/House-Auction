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
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Activities.*
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth


class CustomerProfileItemsListFragment : Fragment() {

    var items :ArrayList<Item> = arrayListOf()
    lateinit var type : String
    val currentCustomer = FirebaseAuth.getInstance().uid.toString()
    val customer = Customer()
    private lateinit var ListView: ListView
    private lateinit var Context: Activity

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_items_list, container, false)
        ListView = view.findViewById<ListView>(R.id.auction_house_items)
        Context = activity as ProfileItemsList
        val text_empty_items_list =  view.findViewById<TextView>(R.id.textView_empty_items_list)
        val btn_auction_item =  view.findViewById<Button>(R.id.btn_auction_item)
        text_empty_items_list.isVisible = false
        btn_auction_item.isVisible = false
        if(items.isEmpty())
            text_empty_items_list.isVisible = true

        ListView.adapter = CustomListAdapter(Context,items)
        ListView.setOnItemClickListener { parent, view, position, id ->
            if(!items.isEmpty()) {
                val intent = Intent(Context, ViewProfileItem::class.java)
                val item = items[position]
                intent.putExtra("Item", item)
                intent.putExtra("Items Type", type)
                startActivity(intent)
            }
        }

        view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh).setOnRefreshListener {
            customer.SetID(currentCustomer)
            if (type.equals("Auctioned")) {
                customer.fetchCustomerAuctionedItems(currentCustomer, ::updateItemsList)
                items = customer.auctionedItems
            }
            else {
                customer.fetchCustomerBiddedItems(currentCustomer, ::updateItemsList)
                items = customer.biddedItems
            }

        }


        view.findViewById<TextView>(R.id.txt_back).setOnClickListener {
            val intent = Intent(Context, CustomerMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            Context.finish()
        }

        view.findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(Context, LoginActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun updateItemsList() {
        ListView.adapter = CustomListAdapter(Context,items)
    }

    private class CustomListAdapter(context: Context,_items:ArrayList<Item>): BaseAdapter(){

        private val mContext:Context
        private val Items:ArrayList<Item>

        init{
            mContext = context
            Items = _items
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
            //View.findViewById<ImageView>(R.id.imageView_house_item).setImageBitmap(BitmapFactory.decodeByteArray(Items[position].ImagesArray[0],0,Items[position].ImagesArray[0].size))
            View.findViewById<ImageView>(R.id.imageView_house_item).setBackgroundResource(R.drawable.round_outline)
            View.findViewById<ImageView>(R.id.imageView_house_item).clipToOutline = true

            return View
        }
    }

}
