package com.example.auctionhouseapp.Objects

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import java.io.Serializable

class Item : Serializable,Comparable<Item> {
    lateinit var ownerId: String
    lateinit var Name: String
    lateinit var Description: String
    lateinit var docID:String
    lateinit var ImagesArray:ArrayList<Bitmap>
    private lateinit var imagesIDs:ArrayList<String>
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
        lastBidderId = Data[Constants.ITEM_LAST_BIDDER] as String?
        lastBid = (Data[Constants.ITEM_LAST_BID_AMOUNT] as Long).toInt()
        return
    }

    fun FetchImages(ToPerform:() -> Unit){

        var NumOfImagesRead:Int = 0
        val MaxImageSize:Long = 1080*1080 * 1000
        ImagesArray = arrayListOf()

        for(i in 0..imagesIDs.size-1){

            FirebaseUtils.firebaseStore.reference
                .child(Constants.STORAGE_ITEM+imagesIDs[i])
                .getBytes(MaxImageSize)
                .addOnSuccessListener { Bytes ->
                    val Bitmap = BitmapFactory.decodeByteArray(Bytes,0,Bytes.size)
                    ImagesArray.add(Bitmap)
                    NumOfImagesRead +=1
                    if(NumOfImagesRead == imagesIDs.size)
                        ToPerform()
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Items Image failed with", exception)
                }
        }
    }

    companion object {
        private val TAG = "Item Object"
    }

    override fun compareTo(other: Item): Int {
        TODO("Not yet implemented")
    }

}