package com.example.auctionhouseapp.Objects

import android.util.Log
import android.content.ContentValues.TAG
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlin.coroutines.coroutineContext

class ItemViewModel : ViewModel() {
    //private var _item: MutableLiveData<Item?> = MutableLiveData<Item?>()
    //lateinit var _itemID:String
    private lateinit var firestore: FirebaseFirestore
    private var _items: MutableLiveData<ArrayList<Item>> = MutableLiveData<ArrayList<Item>>()

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToItems()
    }

    private fun listenToItems() {
        FirebaseUtils.itemsCollectionRef
            .addSnapshotListener { snapshot, e ->
                // if there is an exception we want to skip.
                if (e != null) {
                    Log.w(TAG, "Listen Failed", e)
                    return@addSnapshotListener
                }
                // if we are here, we did not encounter an exception
                if (snapshot != null) {
                    val allItems = ArrayList<Item>()
                    snapshot.documents.forEach {
                        val item = Item(it.data)
                        //val item = it.toObject(Item::class.java)
                        if (item != null)
                            allItems.add(item)

                    }
                    _items.value = allItems
                }
            }
    }


    internal var allItems: MutableLiveData<ArrayList<Item>>
    get() {return _items}
    set(value) {_items=value}

}


//    fun listenToItem(_itemID:String) {
//        firestore.collection("Items").document(_itemID).addSnapshotListener {
//                snapshot, e->
//            // if there is an exception we want to skip.
//            if (e != null) {
//                Log.w(TAG, "Listen Failed", e)
//                return@addSnapshotListener
//            }
//            // if we are here, we did not encounter an exception
//            if (snapshot != null) {
//                // now, we have a populated shapshot
//                val item = snapshot.toObject(Item::class.java)
//                _item.value = item
//            }
//        }
//    }

//    internal var current_item: MutableLiveData<Item?>
//    get() {return _item}
//    set(value) {_item=value}

