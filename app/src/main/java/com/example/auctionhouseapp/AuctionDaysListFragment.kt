package com.example.auctionhouseapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuctionDaysListFragment : Fragment() {

    var House = AuctionHouse()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auction_days_list, container, false)

        //TODO:Add List Action

        return view
    }

    companion object {
        private val TAG = "AuctionDaysList"
    }

}