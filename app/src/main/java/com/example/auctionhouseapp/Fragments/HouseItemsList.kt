package com.example.auctionhouseapp.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Activities.ItemsList
import com.example.auctionhouseapp.Activities.ViewItem
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.google.firebase.auth.FirebaseAuth

class HouseItemsList : Fragment() {

    var Day:AuctionDays = AuctionDays()
    var isRequestedList:Boolean = false
    val HouseId = FirebaseAuth.getInstance().currentUser?.uid
    @RequiresApi(Build.VERSION_CODES.O)
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
            ListView.adapter = CustomListAdapter(Context,Day.ListedItems)
        else
            ListView.adapter = CustomListAdapter(Context,Day.RequestedItems)

        ListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(Context, ViewItem::class.java)
            if(!isRequestedList) {
                var item = Day.ListedItems[position]
                intent.putExtra("Item", item)
                intent.putExtra("ListType",false)
            } else {
                var item = Day.RequestedItems[position]
                intent.putExtra("Item", item)
                intent.putExtra("ListType",true)
            }
            intent.putExtra("HouseID",HouseId)
            intent.putExtra("DayID", Day.DocumentID)
            intent.putExtra("SalesDate", Day.PrintDate())
            intent.putExtra("StartTime", Day.PrintStartTime())
            val userType = UserType.AuctionHouse
            intent.putExtra("Type", 1)
            startActivity(intent)
        }


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