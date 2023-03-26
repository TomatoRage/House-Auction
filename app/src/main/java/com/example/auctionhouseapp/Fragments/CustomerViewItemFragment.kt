package com.example.auctionhouseapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Activities.HouseActivity
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.Extensions.toast


class CustomerViewItemFragment : Fragment() {
    lateinit var SalesDate:String
    lateinit var StartTime:String
    lateinit var item: Item
    lateinit var imageView: ImageView
    lateinit var NextBtn: ImageButton
    lateinit var PrevBtn: ImageButton
    private var position = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_customer_view_item, container, false)
        NextBtn = view.findViewById<ImageButton>(R.id.btn_next_img)
        imageView = view.findViewById<ImageView>(R.id.img_item)
        PrevBtn = view.findViewById<ImageButton>(R.id.btn_prev_img)

        Glide.with(this)
            .load(item._imagesUrls.get(position))
            .into(imageView)
        view.findViewById<TextView>(R.id.item_name).setText(item._name)
        view.findViewById<TextView>(R.id.item_description).setText(item._description)
        view.findViewById<TextView>(R.id.item_sales_day).setText(SalesDate)
        view.findViewById<TextView>(R.id.item_sales_start_time).setText(StartTime)



        NextBtn.setOnClickListener {
            if (position < item._imagesUrls.size - 1) {
                position++
                Glide.with(this)
                    .load(item._imagesUrls.get(position))
                    .into(imageView)

            } else {
                Toast.makeText(context, "No More Images...", Toast.LENGTH_SHORT).show()
            }
        }

        PrevBtn.setOnClickListener {
            if (position > 0) {
                position--

                Glide.with(this)
                    .load(item._imagesUrls.get(position))
                    .into(imageView)
            } else {
                Toast.makeText(context, "No More Images...", Toast.LENGTH_SHORT).show()
            }
        }
    return view
    }
}