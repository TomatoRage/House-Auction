package com.example.auctionhouseapp.Objects

import com.example.auctionhouseapp.User
import com.example.auctionhouseapp.UserType

class Customer: User {

    constructor(Data:HashMap<String,Any>){
        SetData(Data)
        SetType(UserType.Customer)
    }

    constructor(){
        SetType(UserType.Customer)
    }

}