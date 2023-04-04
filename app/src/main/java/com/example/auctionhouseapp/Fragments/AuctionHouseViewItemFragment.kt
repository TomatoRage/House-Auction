package com.example.auctionhouseapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Activities.HouseActivity
import com.example.auctionhouseapp.Activities.ViewItem
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.example.auctionhouseapp.adapter.AddOns.loadingDialog
import com.google.firebase.firestore.FieldValue


class AuctionHouseViewItemFragment : Fragment() {
    lateinit var SalesDate:String
    lateinit var StartTime:String
    lateinit var item: Item
    lateinit var HouseId: String
    lateinit var DayId: String
    lateinit var imageView: ImageView
    lateinit var NextBtn: ImageButton
    lateinit var PrevBtn: ImageButton
    lateinit var dialog:AlertDialog
    private var position = 0
    var isRequestedList = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_auction_house_view_item, container, false)
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
        view.findViewById<TextView>(R.id.item_start_price).setText(item._startingPrice.toString())
        view.findViewById<TextView>(R.id.btn_reject_item).isVisible = false

        if(isRequestedList)
            view.findViewById<Button>(R.id.btn_accept_item).setText("Accpet")
        else
            view.findViewById<Button>(R.id.btn_accept_item).setText("Delete")

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

        view.findViewById<TextView>(R.id.btn_accept_item).setOnClickListener {
            if(isRequestedList) {
                //remvoe from req and add to listed
                item.AddToListedItems(HouseId, DayId)
                item.RemoveFromRequestedItems(HouseId, DayId, ::backToPrevActivity)
                item.UpdateStatus("Accepted", ::toastAccept)

                Toast.makeText(context, "Item Accepted!", Toast.LENGTH_SHORT).show()
            }else{
                //Delete Item Button
                val builder = AlertDialog.Builder(activity as ViewItem)
                val dialogView = layoutInflater. inflate (R.layout.fragment_auction_days_spinner,null)
                builder.setView (dialogView)
                builder.setCancelable (false)
                dialog.show()
                FirebaseUtils.houseCollectionRef
                    .document(HouseId)
                    .collection(Constants.SALES_DAY_COLLECTION)
                    .document(DayId)
                    .update(Constants.LISTED_ITEMS,FieldValue.arrayRemove(item._id))
                    .addOnSuccessListener {

                        FirebaseUtils.itemsCollectionRef.document(item._id).delete()
                            .addOnSuccessListener {
                                dialog.dismiss()
                                backToPrevActivity()
                            }
                            .addOnFailureListener {

                                FirebaseUtils.houseCollectionRef
                                    .document(HouseId)
                                    .collection(Constants.SALES_DAY_COLLECTION)
                                    .document(DayId)
                                    .update(Constants.LISTED_ITEMS,FieldValue.arrayUnion(item._id)).addOnSuccessListener {
                                        Toast.makeText(context, "Failed To Delete", Toast.LENGTH_SHORT).show()
                                    }

                            }

                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed To Delete", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        if (isRequestedList) {
            view.findViewById<TextView>(R.id.btn_reject_item).isVisible = true
            view.findViewById<TextView>(R.id.btn_reject_item).setOnClickListener {
                item.RemoveFromHouseList(Constants.REQUESTED_ITEMS,HouseId,DayId,::backToPrevActivity)
                item.UpdateStatus("Rejected", ::toastReject)
                Toast.makeText(context, "Item Rejected!", Toast.LENGTH_SHORT).show()
            }
        }
    return view
    }


    fun backToPrevActivity() {
        val intent = Intent(context, HouseActivity::class.java)
        startActivity(intent)
    }

    fun toastAccept() {
        Toast.makeText(context, "Item Accepted!", Toast.LENGTH_SHORT).show()
    }

    fun toastReject() {
        Toast.makeText(context, "Item Rejected!", Toast.LENGTH_SHORT).show()
    }
}





