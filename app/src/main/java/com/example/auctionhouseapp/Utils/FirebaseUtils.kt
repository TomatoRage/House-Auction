package com.example.auctionhouseapp.Utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseUtils {
    //val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    //val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    val firebaseDataBase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val userCollectionRef = FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION)
    val houseCollectionRef = FirebaseFirestore.getInstance().collection(Constants.HOUSES_COLLECTION)
    val firebaseStore = FirebaseStorage.getInstance()
    val storageReference = FirebaseStorage.getInstance().reference
}