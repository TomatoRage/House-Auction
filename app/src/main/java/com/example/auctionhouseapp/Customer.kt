package com.example.auctionhouseapp

class Customer:User {

    constructor(Data:HashMap<String,Any>){
        SetData(Data)
        SetType(UserType.Customer)
    }

    constructor(){
        SetType(UserType.Customer)
    }

}