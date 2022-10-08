package com.example.auctionhouseapp

class AuctionHouse: User {

    constructor(Data:HashMap<String,Any>){
        SetData(Data)
        SetType(UserType.AuctionHouse)
    }

    constructor(){
        SetType(UserType.AuctionHouse)
    }

}