package com.example.auctionhouseapp.Objects

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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

    var auctionedImagesFetched = 0
    var biddedImagesFetched = 0

    constructor(Data:MutableMap<String,Any>?){
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchCustomerAuctionedItems(UserID:String, ToPerform:()->Unit) {
        FirebaseUtils.customerCollectionRef
            .document(UserID)
            .get()
            .addOnSuccessListener {  doc ->
                if(doc != null) {
                    if (doc.data?.containsKey("Auctioned Items")!!) {
                        val items: ArrayList<String> =
                            doc.data?.get("Auctioned Items") as ArrayList<String>
                        val numOfItems = items.size
                        for (itemID in items) {
                            FirebaseUtils.itemsCollectionRef
                                .document(itemID)
                                .get()
                                .addOnSuccessListener { doc2 ->
                                    val itemToAdd = Item(doc2.data)
                                    auctionedItems.add(itemToAdd)
                                    checkAllAuctionedItemsFetched(ToPerform, numOfItems)
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("Customer.kt", "Items data read failed with", exception)
                                }
                        }
                    } else {ToPerform()}
                }
            }.addOnFailureListener { exception ->
                Log.d("Customer", "user data read failed with", exception)
            }
    }


    fun checkAllAuctionedItemsFetched(ToPerform: () -> Unit, size:Int) {
        if (auctionedItems.size == size) {
            ToPerform()
        }
    }
        //FetchAuctiondImages(ToPerform)



//    fun FetchAuctiondImages(ToPerform: () -> Unit) {
//        for (item in auctionedItems) {
        //item.FetchImages(1, ::checkAllAuctionedImagesFetched, ToPerform)
//        }
//    }

//    fun checkAllAuctionedImagesFetched(ToPerform: () -> Unit) {
//        auctionedImagesFetched++
//        if (auctionedImagesFetched == auctionedItems.size) {
//            ToPerform()
//        }
//    }


    fun fetchCustomerBiddedItems(UserID:String, ToPerform:()->Unit) {

        FirebaseUtils.customerCollectionRef
            .document(UserID)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    if (doc.data?.containsKey("Bidded Items")!!) {
                        val items: ArrayList<String> =
                            doc.data?.get("Bidded Items") as ArrayList<String>
                        val numOfItems = items.size
                        for (itemID in items) {
                            FirebaseUtils.itemsCollectionRef
                                .document(itemID)
                                .get()
                                .addOnSuccessListener { doc2 ->
                                    val itemToAdd = Item(doc2.data)
                                    biddedItems.add(itemToAdd)
                                    checkAllBiddedItemsFetched(ToPerform, numOfItems)
                                }

                                .addOnFailureListener { exception ->
                                    Log.d("Customer.kt", "Items data read failed with", exception)
                                }
                        }
                    } else {ToPerform()}
                }
            }.addOnFailureListener { exception ->
                Log.d("Customer", "user data read failed with", exception)
            }
    }

    fun checkAllBiddedItemsFetched(ToPerform: () -> Unit, size:Int) {
        if (biddedItems.size == size) {
            ToPerform()
            //FetchBiddedImages(ToPerform)
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun FetchBiddedImages(ToPerform: () -> Unit) {
//        for (item in biddedItems) {
//            item.FetchImages(1, ::checkAllBiddedImagesFetched, ToPerform)
//        }
//    }
//
//    fun checkAllBiddedImagesFetched(ToPerform: () -> Unit) {
//        biddedImagesFetched++
//        if (biddedImagesFetched == biddedItems.size) {
//            ToPerform()
//        }
//    }



}