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
import androidx.core.view.isVisible
import com.example.auctionhouseapp.Activities.*
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.google.firebase.auth.FirebaseAuth


class CustomerProfileItemsListFragment : Fragment() {

    var customer:Customer = Customer()
    var items :ArrayList<Item> = arrayListOf()
    var itemsType:String = String()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_customer_items_list, container, false)
        val ListView = view.findViewById<ListView>(R.id.auction_house_items)
        val Context = activity as CustomerMainActivity
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
                intent.putExtra("Item", items[position])
                startActivity(intent)
            }
        }
        view.findViewById<TextView>(R.id.txt_back).setOnClickListener {
            Context.finish()
        }

        view.findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(Context, LoginActivity::class.java)
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
            View.findViewById<ImageView>(R.id.imageView_house_item).setImageBitmap(BitmapFactory.decodeByteArray(Items[position].ImagesArray[0],0,Items[position].ImagesArray[0].size))
            View.findViewById<ImageView>(R.id.imageView_house_item).setBackgroundResource(R.drawable.round_outline)
            View.findViewById<ImageView>(R.id.imageView_house_item).clipToOutline = true

            return View
        }
    }

}
