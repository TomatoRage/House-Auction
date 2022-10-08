package com.example.auctionhouseapp

import android.media.audiofx.AudioEffect

enum class UserType(val Type:Int){
    Customer(0),AuctionHouse(1);
    companion object {
        fun getByValue(value:Int) = values().first { it.Type == value}
    }
}

open class User {

    protected var Name:String = ""
    protected var Email:String = ""
    protected var Type:UserType = UserType.Customer
    protected var PhoneNumber:String = ""
    protected var Location:String = ""
    protected var UserID:String? = null


    constructor(Data: HashMap<String,Any>){
        SetData(Data)
    }

    constructor()

    fun SetData(Data: HashMap<String,Any>){
        Name = Data[Constants.USER_NAME] as String
        Email = Data[Constants.USER_EMAIL] as String
        Type = UserType.getByValue( Data[Constants.USER_TYPE] as Int)
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