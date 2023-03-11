package com.example.auctionhouseapp.Objects

import android.graphics.BitmapFactory
import android.os.Build
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import java.io.ByteArrayOutputStream
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KFunction1

class Item : Serializable {
    lateinit var ownerId: String
    lateinit var ID: String
    lateinit var Name: String
    lateinit var auctionHouseName:String
    lateinit var Description: String
    lateinit var imagesIDs:ArrayList<String>
    lateinit var imagesUrls:ArrayList<String>
    lateinit var status:String
    var startingPrice: Int = 0
    var lastBidderId: String? = null
    var lastBid:Int = 0


    constructor()

    constructor(itemId:String) {
        ID = itemId
    }
    constructor(Data: MutableMap<String, Any>?) {
        SetData(Data)
    }


    fun SetData(Data: MutableMap<String, Any>?) {
        if (Data == null)
            return
        ownerId = Data[Constants.ITEM_OWNER_ID] as String
        Name = Data[Constants.ITEM_NAME] as String
        auctionHouseName = Data[Constants.ITEM_AUCTION_HOUSE] as String
        Description = Data[Constants.ITEM_DESCRIPTION] as String
        startingPrice = (Data[Constants.ITEM_START_PRICE] as Long).toInt()
        imagesIDs = Data[Constants.ITEM_PHOTOS_LIST] as ArrayList<String>
        imagesUrls = Data[Constants.ITEM_URL_LIST] as ArrayList<String>
        status = Data[Constants.ITEM_STATUS] as String
        if(Data[Constants.ITEM_LAST_BIDDER] != null) {
            lastBidderId = Data[Constants.ITEM_LAST_BIDDER] as String?
            lastBid = (Data[Constants.ITEM_LAST_BID_AMOUNT] as Long).toInt()
        }
        ID = Data[Constants.ITEM_ID] as String
        return
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun FetchImages(NumOfImages:Int, ToPerform1: KFunction1<() -> Unit, Unit>, ToPerform2: () -> Unit={}) {
//
//        var NumOfImagesRead:Int = 0
//        val MaxImageSize:Long = 1080*1080 * 1000
//        var NumToFetch:Int = NumOfImages
//
//
//
//        if(NumOfImages == -1)
//            NumToFetch = imagesIDs.size
//
//        for(i in 0 until NumToFetch){
//            if (ImagesSharedPref.contain(imagesIDs[i])) {
//                ImagesSharedPref.fetchImage(imagesIDs[i])?.let { ImagesArray.add(it) }
//                continue
//            }
//            FirebaseUtils.firebaseStore.reference
//                .child(Constants.STORAGE_ITEM+imagesIDs[i])
//                .getBytes(MaxImageSize)
//                .addOnSuccessListener { Bytes ->
//                    val Bitmap = BitmapFactory.decodeByteArray(Bytes,0,Bytes.size)
//                    var Stream = ByteArrayOutputStream()
//                    Bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,Stream)
//                    ImagesArray.add(Stream.toByteArray())
//                    NumOfImagesRead +=1
//                    if(NumOfImagesRead == NumToFetch)
//                        ToPerform1(ToPerform2)
//                }
//                .addOnFailureListener { exception ->
//                    Log.d("Item.kt", "Items Image failed with", exception)
//                }
//        }
//    }

    fun StoreData(items_list_type:String,HouseID:String, DayID:String, customerID:String, ToPerform:()->Unit={}){
        // 1 - Store item in Items Collection
        val Today = Timestamp(Date())
        /**Store Day Data**/
        val item_id = FirebaseUtils.itemsCollectionRef.document()
            .id
        FirebaseUtils.itemsCollectionRef.document(item_id)
        .set(
            mapOf(
                Constants.ITEM_DESCRIPTION to Description,
                Constants.ITEM_LAST_BID_AMOUNT to lastBid,
                Constants.ITEM_LAST_BID_TIME to Today.toDate(),
                Constants.ITEM_LAST_BIDDER to lastBidderId,
                Constants.ITEM_NAME to Name,
                Constants.ITEM_OWNER_ID to ownerId,
                Constants.ITEM_ID to item_id,
                Constants.ITEM_NUM_IN_QUEUE to 0,
                Constants.ITEM_START_PRICE to startingPrice,
                Constants.ITEM_PHOTOS_LIST to imagesIDs,
                Constants.ITEM_URL_LIST to imagesUrls,
                Constants.ITEM_STATUS to status,
                Constants.ITEM_AUCTION_HOUSE to auctionHouseName,

            )
        ) // 2 - Store item id in the given day of the given auction house
        .addOnSuccessListener {
                FirebaseUtils.houseCollectionRef
                .document(HouseID)
                .collection(Constants.SALES_DAY_COLLECTION)
                .document(DayID)
                    .update(items_list_type,FieldValue.arrayUnion(item_id)).addOnCompleteListener {task->
                    if (task.isSuccessful) {
                        StoreDataInCustomer(Constants.AUCTIONED_ITEMS, item_id, customerID, ToPerform)
                        Log.d("Items.kt" ,"successful item insertion to $items_list_type")
                    } else {
                        Log.d("Items.kt", "failed inserting item to $items_list_type")
                    }
                }

        }
        .addOnFailureListener { exception ->
            Log.d("Item.kt", "item data read failed with", exception)
        }
    }


    fun StoreDataInCustomer(items_list_type:String, item_id:String ,customerID:String, ToPerform:()->Unit={}) {
            FirebaseUtils.customerCollectionRef
                .document(customerID)
                .update(items_list_type,FieldValue.arrayUnion(item_id)).addOnCompleteListener {task->
                    if (task.isSuccessful) {
                        ToPerform()
                        Log.d("Items.kt" ,"successful item insertion to $items_list_type")
                    } else {
                        Log.d("Items.kt", "failed inserting item to $items_list_type")
                    }
                }
        }


    fun RemoveFromRequestedItems(HouseID:String, DayID:String,ToPerform:()->Unit={}) {
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayID)
            .update(Constants.REQUESTED_ITEMS,(FieldValue.arrayRemove(ID)))
            .addOnCompleteListener {task->
                if (task.isSuccessful) {
                    ToPerform()
                    Log.d("Items.kt" ,"successful item remove to ${Constants.REQUESTED_ITEMS}")
                } else {
                    Log.d("Items.kt", "failed remove item to ${Constants.REQUESTED_ITEMS}")
                }
            }
    }

    fun AddToListedItems(HouseID:String, DayID:String,ToPerform:()->Unit={}) {
        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayID)
            .update("Listed Items",(FieldValue.arrayUnion(ID)))
            .addOnCompleteListener {task->
                if (task.isSuccessful) {
                    ToPerform()
                    Log.d("Items.kt" ,"successful item remove to ${Constants.LISTED_ITEMS}")
                } else {
                    Log.d("Items.kt", "failed remove item to ${Constants.LISTED_ITEMS}")
                }
            }
    }

    fun RemoveFromHouseList(items_list_type:String,HouseID:String, DayID:String,ToPerform:()->Unit={}){

        FirebaseUtils.houseCollectionRef
            .document(HouseID)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayID)
            .update(items_list_type,FieldValue.arrayRemove(ID)).addOnCompleteListener {task->
                if (task.isSuccessful) {
                    ToPerform()
                    Log.d("Items.kt" ,"successful item remove to $items_list_type")
                } else {
                    Log.d("Items.kt", "failed remove item to $items_list_type")
                }
            }
    }
    fun UpdateStatus(newStatus:String,ToPerform:()->Unit={}) {
        FirebaseUtils.itemsCollectionRef
            .document(ID)
            .update(
                mapOf(
                    Constants.ITEM_STATUS to newStatus,
                )
            ).addOnSuccessListener { ToPerform() }
            .addOnFailureListener { Log.i("Item.kt", "-E- while updating item's status") }

    }

    fun RemoveFromCustomerList(items_list_type:String, customerID:String, ToPerform:()->Unit={}){

        FirebaseUtils.customerCollectionRef
            .document(customerID)
            .update(items_list_type,FieldValue.arrayRemove(ID)).addOnCompleteListener {task->
                if (task.isSuccessful) {
                    ToPerform()
                    Log.d("Items.kt" ,"successful item remove to $items_list_type")
                } else {
                    Log.d("Items.kt", "failed remove item to $items_list_type")
                }
            }
    }

    companion object {
        private val TAG = "Item Object"
    }

}
