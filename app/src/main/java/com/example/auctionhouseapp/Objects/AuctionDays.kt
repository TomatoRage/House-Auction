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
    var LockBefore:Int = 0
    var ParticipantsNum:Int = 0
    var NumOfSoldItems:Int = 0
    var Status:AuctionDayStatus = AuctionDayStatus.Pending
    var ListedItems:ArrayList<Item> = arrayListOf()
    var RequestedItems:ArrayList<Item> = arrayListOf()

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
        DocumentID = Data[Constants.DAY_ID] as String
        NumOfSoldItems = (Data[Constants.DAY_NUM_OF_SOLD] as Long).toInt()
        for(itemId in Data[Constants.REQUESTED_ITEMS] as ArrayList<String>) {
            val item = Item(itemId)
            RequestedItems.add(item)
        }
        for(itemId in Data[Constants.LISTED_ITEMS] as ArrayList<String>) {
            val item = Item(itemId)
            ListedItems.add(item)
        }

        val Time:Date = Timestamp(Date()).toDate()

        if(StartDate.before(Time)){
            if(NumOfSoldItems < ListedItems.size) {
                Status = AuctionDayStatus.Happening
                return
            }
            if(NumOfSoldItems == ListedItems.size){
                Status = AuctionDayStatus.Occurred
                return
            }
        }else{
            Status = AuctionDayStatus.Pending
            return
        }
    }

    fun updateStatus() {
        val Time:Date = Timestamp(Date()).toDate()
        if(StartDate.before(Time)){
            if(NumOfSoldItems < ListedItems.size) {
                Status = AuctionDayStatus.Happening
                return
            }
            if(NumOfSoldItems == ListedItems.size){
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
        val day_id = FirebaseUtils.houseCollectionRef.document()
            .id
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(day_id)
            .set(
                mapOf(
                    Constants.DAY_NAME to Title,
                    Constants.DAY_START_DATE to StartDate,
                    Constants.DAY_COMMISSION to Commission,
                    Constants.DAY_LOCK_TIME to LockBefore,
                    Constants.DAY_NUM_OF_PARTICIPANTS to ParticipantsNum,
                    Constants.DAY_NUM_OF_SOLD to NumOfSoldItems,
                    Constants.DAY_ID to day_id,
                    Constants.LISTED_ITEMS to ListedItems,
                    Constants.REQUESTED_ITEMS to RequestedItems
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

fun FetchListedItems(HouseID: String, ToPerform: () -> Unit, type:UserType=UserType.Customer) {
        val oldListedItems: ArrayList<Item> = ArrayList(ListedItems)
        val numOfListedItems = ListedItems.size
        var numOfSoldItemsInItemsList =  0
       ListedItems.clear()
        for (item in oldListedItems) {
            FirebaseUtils.itemsCollectionRef
                .document(item._id)
                .get()
                .addOnSuccessListener { doc ->
                    val itemToAdd = Item(doc.data)
                    if (!(type.equals(UserType.Customer) && itemToAdd._status.equals("Sold"))) {
                        ListedItems.add(itemToAdd)
                    } else {
                        numOfSoldItemsInItemsList++
                    }
                    checkAllistedItemsFetched(ToPerform,numOfListedItems,numOfSoldItemsInItemsList)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Items data read failed with", exception)
                }
        }
        if (oldListedItems.isEmpty()) ToPerform()
    }


    fun checkAllistedItemsFetched(ToPerform: () -> Unit, size:Int, numOfSoldItemsInItemsList:Int) {
        if (ListedItems.size == size || NumOfSoldItems == numOfSoldItemsInItemsList) {
            ToPerform()
        }
    }

fun FetchRequestedItems(HouseID: String, ToPerform: () -> Unit) {
        val oldRequestedItems: ArrayList<Item> = ArrayList(RequestedItems)
        val numOfRequestedItems = RequestedItems.size
        RequestedItems.clear()
        //In Case Auction House Did Not Approve Item Till Auction Time:
        updateStatus()
        if (!this.Status.equals(AuctionDayStatus.Pending))
            ToPerform()
        for (item in oldRequestedItems) {
            FirebaseUtils.itemsCollectionRef
                .document(item._id)
                .get()
                .addOnSuccessListener { doc ->
                    val itemToAdd = Item(doc.data)
                    RequestedItems.add(itemToAdd)
                     checkAllRequestedItemsFetched(ToPerform,numOfRequestedItems)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Items data read failed with", exception)
                }
        }
        if (oldRequestedItems.isEmpty()) ToPerform()
    }


    fun checkAllRequestedItemsFetched( ToPerform: () -> Unit, size:Int) {
        if (RequestedItems.size == size) {
            ToPerform()
        }
    }



    companion object {
        private val TAG = "Auction Day"
    }
}