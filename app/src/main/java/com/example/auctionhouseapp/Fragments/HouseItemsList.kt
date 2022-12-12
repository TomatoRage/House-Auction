package com.example.auctionhouseapp.Fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.auctionhouseapp.Activities.ItemsList
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R

class HouseItemsList : Fragment() {

    var Day:AuctionDays = AuctionDays()
    var isRequestedList:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_house_items_list, container, false)
        val Context = activity as ItemsList
        val ListView = view.findViewById<ListView>(R.id.house_items_list)
        if(!isRequestedList)
            view.findViewById<TextView>(R.id.textview_list_title).setText("Listed Items")
        else
            view.findViewById<TextView>(R.id.textview_list_title).setText("Requested Items")
        view.findViewById<Button>(R.id.btn_house_items_list_bck).setOnClickListener { it ->
            parentFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
            Context.finish()
        }
        if(!isRequestedList)
            ListView.adapter = CustomListAdapter(Context,Day.Items)
        else
            ListView.adapter = CustomListAdapter(Context,Day.RequestedItems)

        return view
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

            View.findViewById<TextView>(R.id.textview_house_item_name).setText(Items[position].Name)
            View.findViewById<TextView>(R.id.textView_description).setText(Items[position].Description)
             View.findViewById<ImageView>(R.id.imageView_house_item).setImageBitmap(BitmapFactory.decodeByteArray(Items[position].ImagesArray[0],0,Items[position].ImagesArray[0].size))
            View.findViewById<ImageView>(R.id.imageView_house_item).setBackgroundResource(R.drawable.round_outline)
            View.findViewById<ImageView>(R.id.imageView_house_item).clipToOutline = true

            return View
        }
    }

}