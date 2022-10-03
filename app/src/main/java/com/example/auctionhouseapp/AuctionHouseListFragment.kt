package com.example.auctionhouseapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AuctionHouseListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AuctionHouseListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auction_house_list, container, false)
        view.findViewById<Button>(R.id.btn_log_out).setOnClickListener {
            Firebase.auth.signOut()
            val navLogin = activity as FragmentNavigation
            navLogin.navigateFrag(LoginFragment(),addToStack = false)
        }
        return view
    }


}