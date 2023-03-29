package com.example.auctionhouseapp.Objects

import android.util.Log
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.User
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class AuctionHouse: User , Serializable {

    var Rating:Double = -1.0
    var TotalRaters:Int = -1
    var NextSalesDay:Date? = null
    val Days:ArrayList<AuctionDays> = arrayListOf()
    var isReadHousePrimaryData:Boolean = false
    var isReadHouseDays:Boolean = false
    var profile_img_url:String? = null


    constructor(Data:MutableMap<String,Any>?){
        SetUserData(Data)
        SetType(UserType.AuctionHouse)
    }

    constructor(){
        SetType(UserType.AuctionHouse)
    }

    fun FetchHousePrimaryData(UserID:String, ToPerform:()->Unit, isFetchAll:Boolean = false) {
        FirebaseUtils.houseCollectionRef.document(UserID).get()
            .addOnSuccessListener {   doc ->
                if(doc != null){
                    SetupHouseData(doc.data)
                    isReadHousePrimaryData = true
                    if (isFetchAll) {
                       if (isReadHouseDays)
                           ToPerform()
                    } else {
                        ToPerform()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "user data read failed with", exception)
            }
    }

    fun FetchHouseDays(UserID:String, ToPerform:()->Unit, isFetchAll:Boolean = false) {
        FirebaseUtils.houseCollectionRef.document(UserID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .orderBy(Constants.DAY_START_DATE,Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                for(doc in documents) {
                    val Day = AuctionDays(doc.data)
                    Day.DocumentID = doc.id
                    Day.updateStatus()
                    this.Days.add(Day)
                }
                isReadHouseDays = true
                if (isFetchAll) {
                    if (isReadHousePrimaryData)
                        ToPerform()
                } else {
                    ToPerform()
                }

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "day data read failed with", exception)
            }

    }

    fun FetchHouseData(UserID:String, ToPerform:()->Unit){
        FetchHousePrimaryData(UserID,ToPerform,true)
        FetchHouseDays(UserID,ToPerform,true)
    }

    fun FetchHouseDay(HouseId:String,DayId:String,ToPerform: () -> Unit) {
        FirebaseUtils.houseCollectionRef.document(UserID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayId)
            .get()
            .addOnSuccessListener {
                val Day = AuctionDays(it.data)
                Days.forEach {
                    if (it.DocumentID.equals(DayId)) {
                        Days.remove(it)
                        Days.add(Day)
                    } else {
                        Days.add(Day)
                    }
                }
            }.addOnFailureListener {
                Log.i(TAG, "Failed while fetching day $DayId")
            }

    }
    private fun SetupHouseData(Data:MutableMap<String,Any>?){
        if(Data == null)
            return
        SetUserData(Data)
        TotalRaters = (Data[Constants.HOUSE_NUM_RATERS] as Long).toInt()
        Rating = (Data[Constants.HOUSE_RATING_SUM] as Long).toDouble()/TotalRaters
        NextSalesDay = (Data[Constants.HOUSE_NEXT_SALES_DATE] as Timestamp?)?.toDate()
        profile_img_url = Data[Constants.PROFILE_URL] as String?
    }

    companion object {
        private val TAG = "Auction House"
    }
}