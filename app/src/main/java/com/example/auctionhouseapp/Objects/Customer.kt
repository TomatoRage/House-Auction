package com.example.auctionhouseapp.Objects

import android.util.Log
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.User
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp

class Customer: User {

    val auctionedItems:ArrayList<Item> = arrayListOf()
    val biddedItems:ArrayList<Item> = arrayListOf()
    var isReadCustomerPrimaryData = false
    var isReadCustomerAuctionedItemsData = false
    var isReadCustomerBiddedItemsData = false
    constructor(Data:HashMap<String,Any>){
        SetUserData(Data)
        SetType(UserType.Customer)
    }

    constructor(){
        SetType(UserType.Customer)
    }

    fun fetchCustomerPrimaryData(UserID:String, ToPerform:()->Unit, isFetchAll:Boolean = false) {
        FirebaseUtils.customerCollectionRef.document(UserID).get()
            .addOnSuccessListener {   doc ->
                if(doc != null){
                    SetUserData(doc.data)
                    isReadCustomerPrimaryData = true
                    if (isFetchAll) {
                        if (isReadCustomerAuctionedItemsData && isReadCustomerBiddedItemsData)
                            ToPerform()
                    } else {
                        ToPerform()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d("Customer", "user data read failed with", exception)
            }
    }

    fun fetchCustomerAuctionedItems(UserID:String, ToPerform:()->Unit, isFetchAll:Boolean = false) {
        FirebaseUtils.customerCollectionRef
            .document(UserID)
            .collection(Constants.AUCTIONED_ITEMS)
            .get()
            .addOnSuccessListener {   documents ->
                if(documents != null){
                    for(doc in documents) {
                        val item = Item(doc.data)
                        item.ID = doc.id
                        this.auctionedItems.add(item)
                    }
                    isReadCustomerAuctionedItemsData = true
                    if (isFetchAll) {
                        if (isReadCustomerPrimaryData && isReadCustomerBiddedItemsData)
                            ToPerform()
                    } else {
                        ToPerform()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d("Customer", "user data read failed with", exception)
            }
    }

    fun fetchCustomerBiddedItems(UserID:String, ToPerform:()->Unit, isFetchAll:Boolean = false) {
        FirebaseUtils.customerCollectionRef
            .document(UserID)
            .collection(Constants.BIDDED_ITEMS)
            .get()
            .addOnSuccessListener {   documents ->
                if(documents != null){
                    for(doc in documents) {
                        val item = Item(doc.data)
                        item.ID = doc.id
                        this.biddedItems.add(item)
                    }
                    isReadCustomerBiddedItemsData = true
                    if (isFetchAll) {
                        if (isReadCustomerPrimaryData && isReadCustomerAuctionedItemsData)
                            ToPerform()
                    } else {
                        ToPerform()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d("Customer", "user data read failed with", exception)
            }
    }



}