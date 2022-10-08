package com.example.auctionhouseapp

import com.google.firebase.Timestamp

class AuctionHouse: User {

    var Rating:Double = -1.0
    var TotalRaters:Int = -1
    var NextSalesDay:Timestamp? = null

    constructor(Data:MutableMap<String,Any>?){
        SetData(Data)
        SetType(UserType.AuctionHouse)
    }

    constructor(){
        SetType(UserType.AuctionHouse)
    }

}