package com.example.auctionhouseapp

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

enum class AuctionDayStatus(val Type:Int){
    Occurred(0),Happening(1),Pending(3);
    companion object {
        fun getByValue(value:Int) = values().first { it.Type == value}
    }
}

class AuctionDays: Serializable,Comparable<AuctionDays> {
    lateinit var Title:String
    lateinit var StartDate:Date
    lateinit var DocumentID:String
    var Commission:Double = 0.0
    var LockBefore:Int = -1
    var ParticipantsNum:Int = -1
    var Earnings:Int = -1
    var NumOfItems:Int = -1
    var NumOfRequested:Int = -1
    var NumOfSoldItems:Int = -1
    var Status:AuctionDayStatus = AuctionDayStatus.Pending
    var Items:ArrayList<Item>? = null

    constructor()

    constructor(Data:MutableMap<String,Any>?){
        SetData(Data)
    }

    override fun compareTo(other: AuctionDays): Int {
        if(Status == AuctionDayStatus.Happening && other.Status == AuctionDayStatus.Pending){
            return -1
        }else if(Status == AuctionDayStatus.Happening && other.Status == AuctionDayStatus.Occurred){
            return -1
        }else if(Status == AuctionDayStatus.Happening && other.Status == AuctionDayStatus.Happening){
            return StartDate.compareTo(other.StartDate)
        }else if(Status == AuctionDayStatus.Pending && other.Status == AuctionDayStatus.Occurred){
            return -1
        }else if(Status == AuctionDayStatus.Pending && other.Status == AuctionDayStatus.Happening){
            return 1
        }else if(Status == AuctionDayStatus.Pending && other.Status == AuctionDayStatus.Pending){
            return StartDate.compareTo(other.StartDate)
        }else if(Status == AuctionDayStatus.Occurred && other.Status == AuctionDayStatus.Happening){
            return 1
        } else if(Status == AuctionDayStatus.Occurred && other.Status == AuctionDayStatus.Pending){
            return 1
        }else if(Status == AuctionDayStatus.Occurred && other.Status == AuctionDayStatus.Occurred){
            return StartDate.compareTo(other.StartDate) * -1
        }
        return 0
    }

    fun SetData(Data:MutableMap<String,Any>?){
        if(Data == null)
            return
        Title = Data[Constants.DAY_NAME] as String
        StartDate = (Data[Constants.DAY_START_DATE] as Timestamp).toDate()
        Commission = Data[Constants.DAY_COMMISSION] as Double
        LockBefore = (Data[Constants.DAY_LOCK_TIME] as Long).toInt()
        ParticipantsNum = (Data[Constants.DAY_NUM_OF_PARTICIPANTS] as Long).toInt()
        Earnings = (Data[Constants.DAY_EARNINGS] as Long).toInt()
        NumOfItems = (Data[Constants.DAY_NUM_OF_ITEMS] as Long).toInt()
        NumOfRequested = (Data[Constants.DAY_NUM_OF_REQUESTED] as Long).toInt()
        NumOfSoldItems = (Data[Constants.DAY_NUM_OF_SOLD] as Long).toInt()

        val Time:Date = Timestamp(Date()).toDate()

        if(StartDate.before(Time)){
            if(NumOfSoldItems < NumOfItems) {
                Status = AuctionDayStatus.Happening
                return
            }
            if(NumOfSoldItems == NumOfItems){
                Status = AuctionDayStatus.Occurred
                return
            }
        }else{
            Status = AuctionDayStatus.Pending
            return
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun PrintDate():String{
        val formatter = SimpleDateFormat("dd/MM/yyyy").format(StartDate)
        return formatter.toString()
    }

    fun PrintStartTime():String{
        val formatter = SimpleDateFormat("HH:mm").format(StartDate)
        return formatter.toString()
    }

    fun StoreData(HouseID:String,ToPerform:()->Unit){
        val Today = Timestamp(Date())

        /**Store Day Data**/
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
                    Constants.DAY_NUM_OF_REQUESTED to NumOfRequested,
                    Constants.DAY_NUM_OF_SOLD to NumOfSoldItems
                )
            )
            .addOnSuccessListener {
                /**Get Closest Sales Day**/
                FirebaseUtils.houseCollectionRef
                    .document(HouseID)
                    .collection(Constants.SALES_DAY_COLLECTION)
                    .whereGreaterThan(Constants.DAY_START_DATE,Today).limit(1)
                    .get()
                    .addOnSuccessListener{ docs ->

                        val NextDate = docs.documents[0].data!![Constants.DAY_START_DATE] as Timestamp
                        /**Set Closest Sales Day to house**/
                        FirebaseUtils.houseCollectionRef
                            .document(HouseID)
                            .update(Constants.HOUSE_NEXT_SALES_DATE, NextDate)
                            .addOnSuccessListener {
                                ToPerform()
                            }
                            .addOnFailureListener { exception ->
                                Log.d(TAG, "house data write failed with", exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "day data read failed with", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "day data read failed with", exception)
            }
    }

    fun FetchItems(NumToFetch:Int,HouseID: String,ToPerform: () -> Unit){

        Items = arrayListOf()
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DocumentID)
            .collection(Constants.ITEMS_COLLECTION)
            .limit(NumToFetch.toLong())
            .get()
            .addOnSuccessListener { documents ->
                for(doc in documents){

                    val ToAdd = Item(doc.data)
                    ToAdd.docID = doc.id
                    ToAdd.FetchImages(ToPerform)
                    Items!!.add(ToAdd)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Items data read failed with", exception)
            }
    }

    companion object {
        private val TAG = "Auction Day"
    }
}