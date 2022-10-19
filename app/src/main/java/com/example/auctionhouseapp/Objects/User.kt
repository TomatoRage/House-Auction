package com.example.auctionhouseapp

import com.example.auctionhouseapp.Utils.Constants

enum class UserType(val Type:Int){
    Customer(0),AuctionHouse(1);
    companion object {
        fun getByValue(value:Int) = values().first { it.Type == value}
    }
}

open class User {

    protected lateinit var Name:String
    protected lateinit var Email:String
    protected lateinit var Type:UserType
    protected lateinit var PhoneNumber:String
    protected lateinit var Location:String
    protected lateinit var UserID:String


    constructor(Data: MutableMap<String,Any>?){
        SetData(Data)
    }

    constructor()

    fun SetData(Data: MutableMap<String,Any>?){
        if(Data == null)
            return
        Name = Data[Constants.USER_NAME] as String
        Email = Data[Constants.USER_EMAIL] as String
        Type = UserType.getByValue((Data[Constants.USER_TYPE] as Long).toInt())
        PhoneNumber = Data[Constants.USER_PHONE] as String
        Location = Data[Constants.USER_ADDR] as String
        UserID = Data[Constants.USERID] as String

    }

    fun GetName(): String { return Name }

    fun SetName(Name:String) { this.Name = Name}

    fun GetEmail():String{ return Email }

    fun SetEmail(Email:String) { this.Email = Email}

    fun GetType():UserType{ return Type }

    fun SetType(Type:UserType) { this.Type = Type}

    fun SetType(Type:Int) { this.Type = UserType.getByValue(Type) }

    fun GetPhoneNumber():String{ return PhoneNumber }

    fun SetPhoneNumber(Number:String) { this.PhoneNumber = Number}

    fun GetAddress():String{ return Location }

    fun SetAddress(Address:String) { this.Location = Address }

    fun GetUID():String? { return UserID }

    fun SetID(ID:String) { this.UserID = ID}
}