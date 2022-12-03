package com.example.auctionhouseapp.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.auctionhouseapp.Activities.AuctionItemActivity
import com.example.auctionhouseapp.Activities.ItemsList
import com.example.auctionhouseapp.Activities.LoginActivity
import com.example.auctionhouseapp.Activities.ViewItem
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.FirebaseUtils
import java.text.SimpleDateFormat


class CustomerItemsListFragment : Fragment() {

    var day = AuctionDays()
    lateinit var HouseId:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_customer_items_list, container, false)
        val ListView = view.findViewById<ListView>(R.id.auction_house_items)
        val Context = activity as ItemsList

        ListView.adapter = CustomListAdapter(Context,day.Items)
        ListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(Context, ViewItem::class.java)
            intent.putExtra("Item",day.Items[position])
            intent.putExtra("SalesDate", day.PrintStartTime())
            intent.putExtra("StartTime", day.PrintStartTime())
            intent.putExtra("House ID", HouseId)
            val userType = UserType.Customer
            intent.putExtra("Type", userType)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.txt_back).setOnClickListener {
            Context.finish()
        }

        view.findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(Context, LoginActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.btn_auction_item).setOnClickListener{
            val intent = Intent(Context, AuctionItemActivity::class.java)
            intent.putExtra("Day ID",day.DocumentID)
            intent.putExtra("House ID", HouseId)
            //intent.putExtra("Type", UserType.Customer.Type)
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

            View.findViewById<TextView>(R.id.textview_house_item_name).setText(Items[position].Name)
            View.findViewById<TextView>(R.id.textView_description).setText(Items[position].Description)
            View.findViewById<ImageView>(R.id.imageView_house_item).setImageBitmap(Items[position].ImagesArray[0])
            View.findViewById<ImageView>(R.id.imageView_house_item).setBackgroundResource(R.drawable.round_outline)
            View.findViewById<ImageView>(R.id.imageView_house_item).clipToOutline = true

            return View
        }
    }

}