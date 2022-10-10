package com.example.auctionhouseapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AuctionDaysListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AuctionDaysListFragment : Fragment() {

    val House:AuctionHouse = AuctionHouse()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auction_days, container, false)

        House.FetchHouseData(Firebase.auth.currentUser!!.uid,::PerformAfterData)

        view.findViewById<Button>(R.id.btn_add_day).setOnClickListener {
            //TODO: Navigate to add day screen
        }

        //TODO:Add List Action

        return view
    }

    fun PerformAfterData() {
       //TODO: Update UI after data fetch
    }

    companion object {
        private val TAG = "AuctionDaysList"
    }

}