package com.example.auctionhouseapp

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp
import com.google.type.DateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class AuctionDays {
    lateinit var Title:String
    lateinit var StartDate:Timestamp
    var Commission:Double = 0.0
    var LockBefore:Int = -1
    var ParticipantsNum:Int = -1
    var Earnings:Int = -1
    var NumOfItems:Int = -1
    var NumOfRequested:Int = -1

    constructor()

    constructor(Data:MutableMap<String,Any>?){
        SetData(Data)
    }

    fun SetData(Data:MutableMap<String,Any>?){
        if(Data == null)
            return
        Title = Data[Constants.DAY_NAME] as String
        StartDate = Data[Constants.DAY_START_DATE] as Timestamp
        Commission = Data[Constants.DAY_COMMISSION] as Double
        LockBefore = (Data[Constants.DAY_LOCK_TIME] as Long).toInt()
        ParticipantsNum = (Data[Constants.DAY_NUM_OF_PARTICIPANTS] as Long).toInt()
        Earnings = (Data[Constants.DAY_EARNINGS] as Long).toInt()
        NumOfItems = (Data[Constants.DAY_NUM_OF_ITEMS] as Long).toInt()
        NumOfRequested = (Data[Constants.DAY_NUM_OF_REQUESTED] as Long).toInt()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun PrintDate():String{
        val formatter = SimpleDateFormat("dd/MM/yyyy").format(StartDate.toDate())
        return formatter.toString()
    }

    fun PrintStartTime():String{
        val formatter = SimpleDateFormat("HH:mm").format(StartDate.toDate())
        return formatter.toString()
    }

    fun StoreData(HouseID:String,ToPerform:()->Unit){
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document()
            .set(
                mapOf(
                    Constants.DAY_NAME to Title,
                    Constants.DAY_START_DATE to StartDate,
                    Constants.DAY_COMMISSION to Commission,
                    Constants.DAY_LOCK_TIME to LockBefore,
                    Constants.DAY_NUM_OF_PARTICIPANTS to ParticipantsNum,
                    Constants.DAY_EARNINGS to Earnings,
                    Constants.DAY_NUM_OF_ITEMS to NumOfItems,
                    Constants.DAY_NUM_OF_REQUESTED to NumOfRequested
                )
            )
            .addOnSuccessListener {
                ToPerform()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "day data read failed with", exception)
            }
    }

    companion object {
        private val TAG = "Auction Day"
    }
}