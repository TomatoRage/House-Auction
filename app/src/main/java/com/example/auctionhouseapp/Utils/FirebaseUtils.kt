package com.example.auctionhouseapp.Utils

import com.example.auctionhouseapp.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    val firebaseDataBase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val userCollectionRef = FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION)
    val firebaseStore = FirebaseStorage.getInstance()
    val storageReference = FirebaseStorage.getInstance().reference
}