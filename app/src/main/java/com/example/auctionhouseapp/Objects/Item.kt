package com.example.auctionhouseapp.Objects

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import java.io.ByteArrayOutputStream
import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Item : Serializable,Comparable<Item> {
    lateinit var ownerId: String
    lateinit var Name: String
    lateinit var Description: String
    lateinit var docID:String
    lateinit var ImagesArray:ArrayList<ByteArray>
    lateinit var imagesIDs:ArrayList<String>
    var startingPrice: Int = 0
    var lastBidderId: String? = null
    var lastBid:Int = 0


    constructor()

    constructor(Data: MutableMap<String, Any>?) {
        SetData(Data)
    }


    fun SetData(Data: MutableMap<String, Any>?) {
        if (Data == null)
            return
        ownerId = Data[Constants.ITEM_OWNER_ID] as String
        Name = Data[Constants.ITEM_NAME] as String
        Description = Data[Constants.ITEM_DESCRIPTION] as String
        startingPrice = (Data[Constants.ITEM_START_PRICE] as Long).toInt()
        imagesIDs = Data[Constants.ITEM_PHOTOS_LIST] as ArrayList<String>
        if(Data[Constants.ITEM_LAST_BIDDER] != null) {
            lastBidderId = Data[Constants.ITEM_LAST_BIDDER] as String?
            lastBid = (Data[Constants.ITEM_LAST_BID_AMOUNT] as Long).toInt()
        }
        return
    }

    fun FetchImages(NumOfImages:Int,ToPerform:() -> Unit,TotalItemsToFetch:Int = -1,ItemNum:Int = -1){

        var NumOfImagesRead:Int = 0
        val MaxImageSize:Long = 1080*1080 * 1000
        var NumToFetch:Int = NumOfImages-1
        ImagesArray = arrayListOf()

        if(NumOfImages == -1 || imagesIDs.size-1 < NumToFetch)
            NumToFetch = imagesIDs.size-1

        for(i in 0..NumToFetch){

            FirebaseUtils.firebaseStore.reference
                .child(Constants.STORAGE_ITEM+imagesIDs[i])
                .getBytes(MaxImageSize)
                .addOnSuccessListener { Bytes ->
                    val Bitmap = BitmapFactory.decodeByteArray(Bytes,0,Bytes.size)
                    var Stream = ByteArrayOutputStream()
                    Bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,Stream)
                    ImagesArray.add(Stream.toByteArray())
                    NumOfImagesRead +=1
                    if(NumOfImagesRead == NumToFetch+1 && TotalItemsToFetch == ItemNum)
                        ToPerform()
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Items Image failed with", exception)
                }
        }
    }

    fun StoreData(HouseID:String, DayID:String,ToPerform:()->Unit){

        val Today = Timestamp(Date())
        /**Store Day Data**/
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayID)
            .collection(Constants.REQUESTED_ITEMS_COLLECTION)
            .document()
            .set(
                mapOf(
                    Constants.ITEM_DESCRIPTION to Description,
                    Constants.ITEM_LAST_BID_AMOUNT to lastBid,
                    Constants.ITEM_LAST_BID_TIME to Today.toDate(),
                    Constants.ITEM_LAST_BIDDER to lastBidderId,
                    Constants.ITEM_NAME to Name,
                    Constants.ITEM_OWNER_ID to ownerId,
                    Constants.ITEM_NUM_IN_QUEUE to 0,
                    Constants.ITEM_START_PRICE to startingPrice,
                    Constants.ITEM_PHOTOS_LIST to imagesIDs,
                )
            )
            .addOnSuccessListener {
                ToPerform()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "item data read failed with", exception)
            }
    }

    fun StoreDataIntoItemsList(HouseID:String, DayID:String,ToPerform:()->Unit){

        val Today = Timestamp(Date())
        /**Store Day Data**/
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayID)
            .collection(Constants.ITEMS_COLLECTION)
            .document()
            .set(
                mapOf(
                    Constants.ITEM_DESCRIPTION to Description,
                    Constants.ITEM_LAST_BID_AMOUNT to lastBid,
                    Constants.ITEM_LAST_BID_TIME to Today.toDate(),
                    Constants.ITEM_LAST_BIDDER to lastBidderId,
                    Constants.ITEM_NAME to Name,
                    Constants.ITEM_OWNER_ID to ownerId,
                    Constants.ITEM_NUM_IN_QUEUE to 0,
                    Constants.ITEM_START_PRICE to startingPrice,
                    Constants.ITEM_PHOTOS_LIST to imagesIDs,
                )
            )
            .addOnSuccessListener {
                ToPerform()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "item data read failed with", exception)
            }
    }

    fun DeleteFromItemsList(HouseID:String, DayID:String,ToPerform:()->Unit){

        /**Store Day Data**/
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayID)
            .collection(Constants.ITEMS_COLLECTION)
            .document(docID).delete()
            .addOnSuccessListener {
                ToPerform()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "failed to delete item from Items List", exception)
            }
    }

    fun DeleteFromRequestedList(HouseID:String, DayID:String,ToPerform:()->Unit){

        /**Store Day Data**/
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayID)
            .collection(Constants.REQUESTED_ITEMS_COLLECTION)
            .document(docID).delete()
            .addOnSuccessListener {
                ToPerform()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "failed to delete item from Items List", exception)
            }
    }

    fun clearImagesArray() {
        this.ImagesArray.clear()
    }

    companion object {
        private val TAG = "Item Object"
    }

    override fun compareTo(other: Item): Int {
        TODO("Not yet implemented")
    }

}