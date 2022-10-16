package com.example.auctionhouseapp

import android.util.Log
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.ArrayList

class AuctionHouse: User {

    var Rating:Double = -1.0
    var TotalRaters:Int = -1
    var NextSalesDay:Timestamp? = null
    val Days:ArrayList<AuctionDays> = arrayListOf<AuctionDays>()
    val Items:ArrayList<Item> = arrayListOf<Item>()

    constructor(Data:MutableMap<String,Any>?){
        SetData(Data)
        SetType(UserType.AuctionHouse)
    }

    constructor(){
        SetType(UserType.AuctionHouse)
    }

    fun FetchHouseData(UserID:String, ToPerform:()->Unit){

        var ReadFirstData = false
        var ReadSecondData = false
        var ReadThirdData = false
        var TodayDate = Timestamp(Date())


            FirebaseUtils.userCollectionRef.document(UserID).get()
            .addOnSuccessListener { doc ->
                if(doc != null){
                    SetData(doc.data)
                    if(ReadFirstData && ReadSecondData && ReadThirdData)
                        ToPerform()
                    else if(ReadFirstData && ReadSecondData)
                         ReadThirdData = true
                    else if(ReadFirstData)
                        ReadSecondData = true
                    else
                        ReadFirstData = true
                }

            }.addOnFailureListener { execption ->
                Log.d(TAG, "user data read failed with", execption)
            }

        FirebaseUtils.houseCollectionRef.document(UserID).get()
            .addOnSuccessListener {   doc ->
                if(doc != null){
                    SetupHoduseData(doc.data)
                    if(ReadFirstData && ReadSecondData && ReadThirdData){
                        Days.sort()
                        ToPerform()
                    }else if(ReadFirstData && ReadSecondData)
                        ReadThirdData = true
                    else if(ReadFirstData)
                        ReadSecondData = true
                    else
                        ReadFirstData = true
                }

            }.addOnFailureListener { exception ->
                Log.d(TAG, "user data read failed with", exception)
            }

        FirebaseUtils.houseCollectionRef.document(UserID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .whereLessThanOrEqualTo(Constants.DAY_START_DATE,TodayDate)//.limit(3)
            .orderBy(Constants.DAY_START_DATE,Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                for(doc in documents)
                    this.Days.add(AuctionDays(doc.data))
                if(ReadFirstData && ReadSecondData && ReadThirdData) {
                    Days.sort()
                    ToPerform()
                }else if(ReadFirstData && ReadSecondData)
                    ReadThirdData = true
                else if(ReadFirstData)
                    ReadSecondData = true
                else
                    ReadFirstData = true
            }
            .addOnFailureListener{ exception ->
                Log.d(TAG, "day data read failed with", exception)
            }

        FirebaseUtils.houseCollectionRef.document(UserID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .whereGreaterThan(Constants.DAY_START_DATE,TodayDate)
            .orderBy(Constants.DAY_START_DATE).get()
            .addOnSuccessListener { documents ->
                for(doc in documents)
                    this.Days.add(AuctionDays(doc.data))
                if(ReadFirstData && ReadSecondData && ReadThirdData) {
                    Days.sort()
                    ToPerform()
                }
                else if(ReadFirstData && ReadSecondData)
                    ReadThirdData = true
                else if(ReadFirstData)
                    ReadSecondData = true
                else
                    ReadFirstData = true
            }
            .addOnFailureListener{ exception ->
                Log.d(TAG, "day data read failed with", exception)
            }
    }

    private fun SetupHoduseData(Data:MutableMap<String,Any>?){
        if(Data == null)
            return
        TotalRaters = (Data[Constants.HOUSE_NUM_RATERS] as Long).toInt()
        Rating = (Data[Constants.HOUSE_RATING_SUM] as Long).toDouble()/TotalRaters
        NextSalesDay = Data[Constants.HOUSE_NEXT_SALES_DATE] as Timestamp?
    }
// Odai wants getters and setters for the 3 new variables in Auction House Obj
//TODO
//    public fun getRating():Double? { return this.Rating }
//    public fun setRating(rating:Double) { this.Rating = rating}
//..



    companion object {
        private val TAG = "Auction House"
    }
}