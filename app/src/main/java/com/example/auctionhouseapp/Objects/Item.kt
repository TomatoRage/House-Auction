package com.example.auctionhouseapp.Objects

import android.net.Uri
import com.example.auctionhouseapp.Utils.Constants

class Item {
    private lateinit var ownerId: String
    private lateinit var Name: String
    private lateinit var Description: String
    private var startingPrice: Int = 0
    private lateinit var Image: Uri
    private var isAccepted: Boolean = false
    private lateinit var lastBidderId: String
    private var lastBid:Int = 0


    constructor()

    constructor(Data: MutableMap<String, Any>?) {
        SetData(Data)
    }


    fun SetData(Data: MutableMap<String, Any>?) {
        if (Data == null)
            return
        ownerId = Data[Constants.ITEM_OWNER_ID] as String
        Name = Data[Constants.ITEM_NAME] as String
        Description = Data[Constants.ITEM_DESCRIPTION] as String
        startingPrice = Data[Constants.ITEM_START_PRICE] as Int
        Image = Data[Constants.ITEM_IMAGE] as Uri
        isAccepted = Data[Constants.ITEM_IS_ACCEPTED] as Boolean
        lastBidderId = Data[Constants.ITEM_LAST_BIDDER] as String
        lastBid = Data[Constants.ITEM_LAST_BID_AMOUNT] as Int
        return
    }
    fun getOwner():String {return this.ownerId}
    fun getName():String {return this.Name}
    fun getDescription():String {return this.Description}
    fun getStartingPrice():Int {return this.startingPrice}
    fun getIsAccepted():Boolean {return this.isAccepted}
    fun getLastBidderId():String {return this.lastBidderId}
    fun getLastBidAmount():Int {return this.lastBid}


    fun setOwner(owner:String) {this.ownerId = owner}
    fun setName(name:String) {this.Name = name}
    fun setDescription(description:String) {this.Description = description}
    fun setStartingPrice(startingPrince:Int) {this.startingPrice = startingPrice}
    fun setIsAccepted(isAccepted:Boolean) {this.isAccepted = isAccepted}
    fun setImage(image:Uri) {this.Image = image}
    fun setLastBidderId(lastBidderId:String) {this.lastBidderId = lastBidderId}
    fun setLastBidAmount(lastBid:Int) {this.lastBid = lastBid}

}