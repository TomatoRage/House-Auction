package com.example.auctionhouseapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.ktx.FirebaseAuthKtxRegistrar
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
        view.findViewById<Button>(R.id.btn_add_day).setOnClickListener {
            FetchUserData()
        }
        return view
    }

    fun FetchUserData(){
        FirebaseFirestore.getInstance()
            .collection(Constants.USER_COLLECTION)
            .document((Firebase.auth.currentUser!!.uid))
            .get()
            .addOnSuccessListener { document ->

                if(document != null){
                    House.SetData(document.data)
                }

            }.addOnFailureListener { execption ->
                Log.d(TAG,"get failed with" , execption)
            }
    }

    companion object {
        private val TAG = "AuctionDaysList"
    }

}