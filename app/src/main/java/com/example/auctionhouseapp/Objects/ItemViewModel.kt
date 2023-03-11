package com.example.auctionhouseapp.Objects

import android.util.Log
import android.content.ContentValues.TAG
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ItemViewModel : ViewModel() {
    private var _item: MutableLiveData<Item?> = MutableLiveData<Item?>()
    private lateinit var firestore : FirebaseFirestore

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToItem("dd")
    }

    private fun listenToItem(itemID:String) {
        firestore.collection("Items").document(itemID).addSnapshotListener {
                snapshot, e->
            // if there is an exception we want to skip.
            if (e != null) {
                Log.w(TAG, "Listen Failed", e)
                return@addSnapshotListener
            }
            // if we are here, we did not encounter an exception
            if (snapshot != null) {
                // now, we have a populated shapshot
                val item = snapshot.toObject(Item::class.java)
                _item.value = item
            }
        }
    }

    internal var current_item: MutableLiveData<Item?>
    get() {return _item}
    set(value) {_item=value}

}