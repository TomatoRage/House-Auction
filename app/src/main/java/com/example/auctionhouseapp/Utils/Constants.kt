package com.example.auctionhouseapp.Utils

/** This file contains global constants to be used in the project i.e. Data base fields**/

object Constants{
    //Collections' Names
    const val USER_COLLECTION:String                = "Users" // Collection Contains all users
    const val HOUSES_COLLECTION:String              = "Houses" // Collection Contains auction houses additional data
    const val SALES_DAY_COLLECTION:String           = "Sales Day"// Collection Contains Sales Days per Auction House
    const val REQUESTED_ITEMS_COLLECTION:String     = "Requested Items"// Collection Contains Requested Items per Day
    const val ITEMS_COLLECTION:String               = "Items" // Collection Contains Accepted Items per Day

    //Storage Directories
     const val STORAGE_ITEM:String                  = "Items/" // Items Dir in Storage
     const val STORAGE_HOUSE:String                  = "Houses/" // Houses Dir in Storage

    //Users' Data
    const val USER_EMAIL:String                     = "Email" // User Email
    const val USER_NAME:String                      = "Full Name" // User Full Name
    const val USER_TYPE:String                      = "Type" // User Type - Customer/Auction House
    const val USER_PHONE:String                     = "Phone Number"// User Phone Number
    const val USER_ADDR:String                      = "Location" //User Address
    const val USERID:String                         = "User ID" // User ID From Auth

    //Auction Houses' Data + Users' Data
    const val HOUSE_DESCRIPTION:String              = "Description" //House Description (In Collection)
    const val HOUSE_RATING_SUM:String               = "Rating" // The Sum Of Ratings For A House
    const val HOUSE_NUM_RATERS:String               = "Total Raters"// No' Of Users Rated The House
    const val HOUSE_NEXT_SALES_DATE:String          = "Next Sales Day" // The Next Upcoming Sales Day Date

    //Auction Days' Data
    const val DAY_NAME:String                       = "Title" // Auction Day Title
    const val DAY_START_DATE:String                 = "Start Date" // Auction Day Start Date and Time
    const val DAY_COMMISSION:String                 = "Commission" // Auction House Commission on Auction Day
    const val DAY_LOCK_TIME:String                  = "Lock Time" // Hours Num To Close Participation in Auction
    const val DAY_NUM_OF_PARTICIPANTS:String        = "Participants" // No' Of Already Participated Customers
    const val DAY_EARNINGS:String                   = "Total Earnings" // Total Earnings On Auction Day Commission
    const val DAY_NUM_OF_ITEMS:String               = "Listed Items" // No' Of Accepted Items
    const val DAY_NUM_OF_REQUESTED:String           = "Requested Items" // No' Of Requested Items
    const val DAY_NUM_OF_SOLD:String                = "Sold Items" // No' of sold items so far

    //Items' Data
    const val ITEM_NAME:String                      = "Name" // Item Name
    const val ITEM_DESCRIPTION:String               = "Description" // Item Description
    const val ITEM_START_PRICE:String               = "Starting Price" // Bid Starting Price
    const val ITEM_PHOTOS_LIST:String               = "Photos" // Array Of Photos IDs
    const val ITEM_OWNER_ID:String                  = "Owner ID" // UserID Of Item Owner
    const val ITEM_LAST_BID_AMOUNT:String           = "Last Bid" // Last Amount Of Money Bidded
    const val ITEM_LAST_BIDDER:String               = "Last Bidder ID " // UserID Of The Last Bidder
    const val ITEM_NUM_IN_QUEUE:String              = "Pos In Queue" // Holds The No' Of Items Before Current Item
    const val ITEM_IS_ACCEPTED:String               = "Is Accepted" // Identifier between accepted & not accepted Items

}