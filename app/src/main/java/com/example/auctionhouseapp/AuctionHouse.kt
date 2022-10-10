package com.example.auctionhouseapp

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.reflect.KFunction0

class AuctionHouse: User {

    var Rating:Double = -1.0
    var TotalRaters:Int = -1
    var NextSalesDay:Timestamp? = null
    val Days:ArrayList<AuctionDays> = arrayListOf<AuctionDays>()

    constructor(Data:MutableMap<String,Any>?){
        SetData(Data)
        SetType(UserType.AuctionHouse)
    }

    constructor(){
        SetType(UserType.AuctionHouse)
    }

    fun FetchHouseData(UserID:String, ToPerform:()->Unit){

        val db = FirebaseFirestore.getInstance()
        var ReadFirstData = false
        var ReadSecondData = false

        db.collection(Constants.USER_COLLECTION).document(UserID).get()
            .addOnSuccessListener { doc ->
                if(doc != null){
                    SetData(doc.data)
                    if(ReadFirstData && ReadSecondData){
                        ToPerform()
                    }else if(ReadFirstData){
                        ReadSecondData = true
                    }else {
                        ReadFirstData = true
                    }
                }

            }.addOnFailureListener { execption ->
                Log.d(TAG, "user data read failed with", execption)
            }

        db.collection(Constants.HOUSES_COLLECTION).document(UserID).get()
            .addOnSuccessListener { doc ->
                if(doc != null){
                    SetupHoduseData(doc.data)
                    if(ReadFirstData && ReadSecondData){
                        ToPerform()
                    }else if(ReadFirstData){
                        ReadSecondData = true
                    }else {
                        ReadFirstData = true
                    }
                }

            }.addOnFailureListener { execption ->
                Log.d(TAG, "user data read failed with", execption)
            }

       /* db.collection(Constants.HOUSES_COLLECTION)
            .document(UserID)
            .collection(Constants.SALES_DAY_COLLECTION)*/
    }

    private fun SetupHoduseData(Data:MutableMap<String,Any>?){
        if(Data == null)
            return
        TotalRaters = (Data[Constants.HOUSE_NUM_RATERS] as Long).toInt()
        Rating = (Data[Constants.HOUSE_RATING_SUM] as Long).toDouble()/TotalRaters
        NextSalesDay = Data[Constants.HOUSE_NEXT_SALES_DATE] as Timestamp?
    }

    companion object {
        private val TAG = "Auction House"
    }
}