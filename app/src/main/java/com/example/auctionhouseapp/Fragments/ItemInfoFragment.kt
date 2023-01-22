package com.example.auctionhouseapp.Fragments


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.auctionhouseapp.Activities.*
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.Utils.Constants
import java.text.SimpleDateFormat


class ItemInfoFragment : Fragment() {
    lateinit var SalesDate:String
    lateinit var StartTime:String
    var StartPrice = 0
    lateinit var userType: UserType
    lateinit var RemoveItemButton:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_item_info, container, false)
        view.findViewById<TextView>(R.id.item_sales_day).setText(SalesDate)
        view.findViewById<TextView>(R.id.item_sales_start_time).setText(StartTime)

        val Context = activity as ViewItem

        RemoveItemButton = view.findViewById<Button>(R.id.btn_remove_item)

        if (userType == UserType.Customer) {
            view.findViewById<TextView>(R.id.item_start_price).isVisible = false
            view.findViewById<TextView>(R.id.txt_start_price).isVisible = false
            RemoveItemButton.isVisible = false
        } else {
            view.findViewById<TextView>(R.id.item_start_price).setText(StartPrice)
            RemoveItemButton.setOnClickListener {
                DeleteItem(Context)
            }
        }
        return view
    }

    fun DeleteItem(Context:ViewItem){
        val builder = AlertDialog.Builder(Context)

        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete this day?")
        builder.setPositiveButton("Confirm", DialogInterface.OnClickListener { dialog, id ->
            FirebaseUtils
                .houseCollectionRef
                .document(FirebaseUtils.firebaseUser!!.uid)
                .collection(Constants.SALES_DAY_COLLECTION)
                .document(Context.day.DocumentID)
                .collection(Constants.ITEMS_COLLECTION)
                .document(Context.item.docID).delete()
                .addOnSuccessListener {

                    FirebaseUtils
                        .houseCollectionRef
                        .document(FirebaseUtils.firebaseUser.uid)
                        .collection(Constants.SALES_DAY_COLLECTION)
                        .document(Context.day.DocumentID) //TODO:Decrease Num Of Items

                    FirebaseUtils
                        .houseCollectionRef
                        .document(FirebaseUtils.firebaseUser.uid)
                        .collection(Constants.ITEMS_COLLECTION)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Item data delete failed with", exception)

                }

            //TODO: FINISH DELETE

        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        val alert = builder.create()
        alert.show()
    }

    companion object {
        private val TAG = "View Item Info Fragemnt"
    }
}