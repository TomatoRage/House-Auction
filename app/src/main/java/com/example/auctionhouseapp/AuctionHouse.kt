package com.example.auctionhouseapp

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.reflect.KFunction0

class AuctionHouse: User {

    var Rating:Double = -1.0
    var TotalRaters:Int = -1
    var NextSalesDay:Timestamp? = null

    constructor(Data:MutableMap<String,Any>?){
        SetData(Data)
        SetType(UserType.AuctionHouse)
    }

    constructor(){
        SetType(UserType.AuctionHouse)
    }

    fun FetchHouseData(UserID:String, ToPerform:()->Unit){

        val db = FirebaseFirestore.getInstance()
        var ReadBothData = false

        db.collection(Constants.USER_COLLECTION).document(UserID).get()
            .addOnSuccessListener { doc ->
                if(doc != null){
                    SetData(doc.data)
                    if(ReadBothData){
                        ToPerform()
                    }
                    ReadBothData = true
                }

            }.addOnFailureListener { execption ->
                Log.d(TAG, "user data read failed with", execption)
            }

        db.collection(Constants.HOUSES_COLLECTION).document(UserID).get()
            .addOnSuccessListener { doc ->
                if(doc != null){
                    SetupHoduseData(doc.data)
                    if(ReadBothData){
                        ToPerform()
                    }
                    ReadBothData = true
                }

            }.addOnFailureListener { execption ->
                Log.d(TAG, "user data read failed with", execption)
            }
    }

    private fun SetupHoduseData(Data:MutableMap<String,Any>?){
        TotalRaters = (Data!![Constants.HOUSE_NUM_RATERS] as Long).toInt()
        Rating = (Data[Constants.HOUSE_RATING_SUM] as Long).toDouble()/TotalRaters
        NextSalesDay = Data[Constants.HOUSE_NEXT_SALES_DATE] as Timestamp?
    }

    companion object {
        private val TAG = "Auction House"
    }
}