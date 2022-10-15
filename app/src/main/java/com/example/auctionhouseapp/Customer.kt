package com.example.auctionhouseapp

import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.example.auctionhouseapp.Utils.FirebaseUtils.firebaseUser
import com.example.auctionhouseapp.Utils.FirebaseUtils.userCollectionRef

class Customer:User {

    constructor(Data:HashMap<String,Any>){
        SetData(Data)
        SetType(UserType.Customer)
    }

    constructor(){
        SetType(UserType.Customer)
    }

}