package com.example.auctionhouseapp.Fragments

import android.accessibilityservice.GestureDescription
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Activities.ItemsList
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.Objects.ItemViewModel
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import nl.dionsegijn.konfetti.KonfettiView
import java.lang.reflect.Field
import java.util.*


class ItemViewBidFragment : Fragment() {
    lateinit var item: Item
    lateinit var imageView: ImageView
    lateinit var NextBtn: ImageButton
    lateinit var PrevBtn: ImageButton
    lateinit var BidBtn: Button
    lateinit var EditBid: EditText
    lateinit var RemainingTime: TextView
    lateinit var RemainingTimeText: TextView
    lateinit var LastBid:TextView
    lateinit var LastBidText:TextView
    lateinit var StartingPrice:TextView
    lateinit var RateAuctionHouseText: TextView
    lateinit var EarningText:TextView
    lateinit var viewKonfetti:KonfettiView
    lateinit var HouseId:String
    lateinit var DayId:String
    lateinit var userType:UserType
    lateinit var intent:Intent
    private var position = 0
    var Commission:Double = 0.1
    private  lateinit var viewModel: ItemViewModel
    private var MaxBid:Int = 0
    private val currentCustomer = FirebaseAuth.getInstance().uid.toString()
    private var currentCustomerCash = -1
    private var START_MILLI_SECONDS = 30000L
    private lateinit var countdown_timer: CountDownTimer
    private var time_in_milli_seconds = 0L
    private var isCustomerInvolvedInAuction:Boolean = false
    private val bidPattern = """\d+"""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_view_item_bid, container, false)
        NextBtn = view.findViewById<ImageButton>(R.id.btn_next_img)
        imageView = view.findViewById<ImageView>(R.id.img_item)
        PrevBtn = view.findViewById<ImageButton>(R.id.btn_prev_img)
        BidBtn = view.findViewById<Button>(R.id.btn_bid)
        LastBid = view.findViewById<TextView>(R.id.txt_last_bid_num)
        EditBid = view.findViewById<EditText>(R.id.edit_txt_bid)
        RemainingTime = view.findViewById<TextView>(R.id.txt_remaining_time_num)
        viewKonfetti = view.findViewById(R.id.viewKonfetti)
        LastBidText = view.findViewById<TextView>(R.id.txt_last_bid)
        RemainingTimeText = view.findViewById<TextView>(R.id.txt_remaining_time)
        StartingPrice = view.findViewById<TextView>(R.id.txt_starting_price)
        EarningText = view.findViewById<TextView>(R.id.txt_earning_value)
        RateAuctionHouseText = view.findViewById<TextView>(R.id.txt_rate_auction_house)
        val backBtn = activity?.findViewById<TextView>(R.id.txt_back)

        EarningText.isVisible = false
        RateAuctionHouseText.isVisible= false
        // Hide Bidding ability for Auction House User
        if (userType.equals(UserType.AuctionHouse) || currentCustomer.equals(item._ownerId)) {
            BidBtn.isVisible = false
            EditBid.isVisible = false
        }
        else {
            updateCustomerCash()
        }

        if (backBtn != null) {
            backBtn.setOnClickListener {
                    intent = Intent(context, ItemsList::class.java)
                    intent.putExtra("DayId",DayId)
                    intent.putExtra("HouseId", HouseId)
                    intent.putExtra("Type", userType.ordinal)
                if (item._status.equals("Sold") && userType.equals(UserType.Customer)) {
                    Toast.makeText(context, "Removing Item..", Toast.LENGTH_SHORT).show()

//                    item.RemoveFromHouseList(
//                        Constants.LISTED_ITEMS,
//                        HouseId,
//                        DayId,
//                        ::goToItemsList
//                    )
                    startActivity(intent)
                    activity?.finish()
                } else {
                        startActivity(intent)
                        activity?.finish()
                }
            }
        }
        Glide.with(this)
            .load(item._imagesUrls.get(position))
            .into(imageView)
        view.findViewById<TextView>(R.id.item_name).setText(item._name)
        view.findViewById<TextView>(R.id.item_description).setText(item._description)
        StartingPrice.setText(item._startingPrice.toString())

        NextBtn.setOnClickListener {
            if (position < item._imagesUrls.size - 1) {
                position++
                Glide.with(this)
                    .load(item._imagesUrls.get(position))
                    .into(imageView)

            } else {
                Toast.makeText(context, "No More Images...", Toast.LENGTH_SHORT).show()
            }
        }

        PrevBtn.setOnClickListener {
            if (position > 0) {
                position--

                Glide.with(this)
                    .load(item._imagesUrls.get(position))
                    .into(imageView)
            } else {
                Toast.makeText(context, "No More Images...", Toast.LENGTH_SHORT).show()
            }
        }

        RateAuctionHouseText.setOnClickListener{
            ShowDialog()
            RateAuctionHouseText.isVisible= false
        }
        startTimer(START_MILLI_SECONDS)
        MaxBid = item._lastBid
        LastBid.setText(MaxBid.toString())
        return view
    }


    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                    if (currentCustomer.equals(item._lastBidderId)) {
                        BidBtn.isVisible = false
                        EditBid.isVisible = false
                        LastBidText.setText("  You Won The Auction")
                        LastBidText.setTextColor(Color.RED)
                        LastBid.isVisible = false
                        RemainingTime.isVisible = false
                        RemainingTimeText.text = "   Congratulations!"
                        RemainingTimeText.setTextColor(Color.RED)
                        loadConfeti()
                        transferCash()
                        updateItemWrapper()
                        item.StoreDataInCustomer(Constants.BIDDED_ITEMS, item._id, currentCustomer)
                        incrementDaySoldItems()
                    } else {
//                        if (item._lastBid == 0) {
//                            updateItem("Unkown")
//                            incrementDaySoldItems()
//                        }

                        BidBtn.isVisible = false
                        EditBid.isVisible = false
                        RemainingTime.isVisible = false
                        LastBidText.text = "Sold For: "
                        if (userType.equals(UserType.Customer)) {
                            RemainingTimeText.text = "Sorry! You Lose The Auction"
                            if (item._ownerId.equals(currentCustomer)) {
                                RemainingTimeText.text = "Item Sold"
                            }
                        } else {
                            RemainingTimeText.text = "Item Sold"
                            RemainingTimeText.setTextColor(Color.GREEN)
                            EarningText.isVisible = true
                            val value = item._lastBid * item._commission
                            val Number = SpannableString(value.toString())
                            Number.setSpan(ForegroundColorSpan(Color.GREEN), 0, value.toString().length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            EarningText.text = TextUtils.concat("You Earned: ",Number)
                        }
                        if (item._lastBid == 0) {
                            RemainingTimeText.text = "Item Not Sold"
                            RemainingTimeText.setTextColor(Color.RED)
                        }
                    }
                        RateAuctionHouseText.isVisible= true
                        if (userType.equals(UserType.AuctionHouse))
                            RateAuctionHouseText.isVisible = false

                }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTimeUI()

            }
        }
        countdown_timer.start()
    }

    private fun resetTimer() {
        //countdown_timer.cancel()
        startTimer(START_MILLI_SECONDS)
    }

    private fun updateTimeUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        RemainingTime.text = "$minute:$seconds"
    }

    private fun updateTimeFirebase(time:Date) {
        FirebaseUtils.itemsCollectionRef
            .document(item._id)
            .update(Constants.ITEM_LAST_BID_TIME, time)
            .addOnSuccessListener {
                Log.i("ItemViewBidFragment.kt", "update max bid time")
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Failed to update max bid time !!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateMaxBidFirebase(bidAmount:Int) {
        FirebaseUtils.itemsCollectionRef
            .document(item._id)
            .update(Constants.ITEM_LAST_BID_AMOUNT, bidAmount.toLong())
            .addOnSuccessListener {
                Toast.makeText(context, "Highest Bid So Far !!", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Failed to update your bid !!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateLastBidderID() {
        FirebaseUtils.itemsCollectionRef
            .document(item._id)
            .update(Constants.ITEM_LAST_BIDDER, currentCustomer)
            .addOnSuccessListener {
                Log.i("ItemViewBidFragment.kt", "update last bidder to $currentCustomer")
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Failed to update last bidder!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun incrementDaySoldItems() {
        FirebaseUtils.houseCollectionRef
            .document(HouseId)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayId)
            .get()
            .addOnSuccessListener {
                val numOfSoldItems = AuctionDays(it.data).NumOfSoldItems
                FirebaseUtils.houseCollectionRef
                    .document(HouseId)
                    .collection(Constants.SALES_DAY_COLLECTION)
                    .document(DayId)
                    .update(Constants.DAY_NUM_OF_SOLD,numOfSoldItems+1)
                    .addOnSuccessListener {
                        Log.i("ItemViewBidFragment.k", "Successfully Incrementing No. of sold items")
                    }.addOnFailureListener {
                        Log.i("ItemViewBidFragment.k", "Failed to Increment No. of sold items")
                    }

            }.addOnFailureListener {
                Log.i("ItemViewBidFragment.k", "Failed to read  No. of sold items")
            }


    }
    private fun loadConfeti() {
        viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(nl.dionsegijn.konfetti.models.Shape.Square, nl.dionsegijn.konfetti.models.Shape.Circle)
            .addSizes(nl.dionsegijn.konfetti.models.Size(12))
            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
            .streamFor(300, 5000L)
    }

    private fun updateItemWrapper() {
        FirebaseUtils.customerCollectionRef
            .document(currentCustomer)
            .get()
            .addOnSuccessListener {
                val customer = Customer(it.data)
                val customer_phone = customer.GetPhoneNumber()
                updateItem(customer_phone)
            } .addOnFailureListener {
            Log.i("ItemViewBidFragment.kt", "Error! failed get customer tel")
        }
        // update status locally also avoid any error when moving backwards
        item._status = "Sold"
    }

    fun updateItem(customer_phone:String) {
        FirebaseUtils.itemsCollectionRef
            .document(item._id)
            .update(
                mapOf(
                    Constants.ITEM_STATUS to  "Sold",
                    Constants.ITEM_WINNER_PHONE to customer_phone
                )
            )
            .addOnSuccessListener {
                Log.i("ItemViewBidFragment.kt", "update item status")
            }
            .addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Error! failed update item status")
            }
    }

    private fun goToItemsList() {
        startActivity(intent)
        activity?.finish()
    }
    private fun updateCustomerCash(ToPerform:()->Unit={}) {
        FirebaseUtils.customerCollectionRef
            .document(currentCustomer)
            .get()
            .addOnSuccessListener {
                val customer = Customer(it.data)
                currentCustomerCash = customer.getCash()
                ToPerform()
            }
            .addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Failed to update winner name")
            }
    }

    fun ShowDialog() {
        val popDialog: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val linearLayout = LinearLayout(requireActivity())
        val rating = RatingBar(requireActivity())
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        rating.layoutParams = lp
        rating.numStars = 5
        rating.stepSize = 1f

        //add ratingBar to linearLayout
        linearLayout.addView(rating)
        popDialog.setIcon(android.R.drawable.btn_star_big_on)
        popDialog.setTitle("Add Rating: ")

        //add linearLayout to dailog
        popDialog.setView(linearLayout)
        rating.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar, v, b -> println("Rated val:$v") }


        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
            DialogInterface.OnClickListener { dialog, which ->
                val auctionHouseRating = rating.progress
                updateAuctionHouseRating(auctionHouseRating)
                dialog.dismiss()
                dialog.cancel()
            }) // Button Cancel
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        popDialog.create()
        popDialog.show()
    }

    private fun updateAuctionHouseRating(auctionHouseRating: Int) {
        FirebaseUtils.houseCollectionRef
            .document(HouseId)
            .get()
            .addOnSuccessListener {
                val auctionHouse = AuctionHouse(it.data)
                val totalRaters = auctionHouse.TotalRaters
                val totalRating = totalRaters*auctionHouse.Rating
                FirebaseUtils.houseCollectionRef
                    .document(HouseId)
                    .update(
                        mapOf(
                            Constants.HOUSE_RATING_SUM to (totalRating+auctionHouseRating),
                            Constants.HOUSE_NUM_RATERS to (totalRaters+1)
                        )

                    )
                    .addOnSuccessListener {
                        Log.i("ItemViewBidFragment.kt", "updating house Rating")
                    }
                    .addOnFailureListener {
                        Log.i("ItemViewBidFragment.kt", "Failed updating house Rating")
                    }

            }.addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Failed updating house Rating")
            }

    }

    private fun transferCash() {
        //Transfer money for the winner to the owner

        //1- move money out from winner account
        FirebaseUtils.customerCollectionRef
            .document(currentCustomer)
            .update(Constants.USER_CASH,FieldValue.increment((-1*item._lastBid).toLong()))
            .addOnSuccessListener {
            //Toast.makeText(activity, "transferring money from your account...", Toast.LENGTH_SHORT).show()
            Log.i("ItemViewBidFragment.kt", "transferring money from your account")
            }.addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Failed get cash for transferring")
            }

        //2- move commission into auction house account
        FirebaseUtils.houseCollectionRef
            .document(HouseId)
            .update(Constants.USER_CASH,FieldValue.increment((Commission*item._lastBid).toLong()))
            .addOnSuccessListener {
                Log.i("ItemViewBidFragment.kt", "transferring commission money to auction house")
            } .addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Failed get cash for transferring")
            }

        FirebaseUtils.houseCollectionRef
            .document(HouseId)
            .collection(Constants.SALES_DAY_COLLECTION)
            .document(DayId)
            .update(Constants.DAY_TOTAL_EARNINGS,FieldValue.increment((item._lastBid*Commission).toLong()))
            .addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Failed update total earnings for auction house")
            }

        //3- move the rest of the money into owner account
        FirebaseUtils.customerCollectionRef
            .document(item._ownerId)
            .update(Constants.USER_CASH,FieldValue.increment((item._lastBid - Commission*item._lastBid).toLong()))

            .addOnSuccessListener {
                Log.i("ItemViewBidFragment.kt", "transferring profit to owner account")
            }.addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Failed get cash for transferring")
            }
    }

    private fun checkInput() {
        if (EditBid.text.isEmpty()) {
            Toast.makeText(activity, "Bid can't be empty!", Toast.LENGTH_SHORT).show()
            return
        }
        if (!EditBid.text.matches(bidPattern.toRegex())) {
            Toast.makeText(activity, "Enter Valid Bid", Toast.LENGTH_SHORT).show()
            return
        }
        bid()
    }

    private fun bid() {
        val bid = EditBid.text.toString()
        isCustomerInvolvedInAuction = true
        if (bid.isEmpty()) {
            Toast.makeText(context, "Insert Valid Bid", Toast.LENGTH_SHORT).show()
        } else {
            val bidAmount = bid.toInt()
            if (currentCustomerCash == -1)
                updateCustomerCash(::bid)
            if (currentCustomerCash < bidAmount) {
                Toast.makeText(
                    context,
                    "Bid Blocked!\nYou Do Not Have Enough Cash",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            if (bidAmount > MaxBid && bidAmount > item._startingPrice) {
                //resetTimer()
                val BidTime: Date = Timestamp(Date()).toDate()
                item._lastBid = bidAmount
                item._last_bid_time = BidTime
                MaxBid = bidAmount
                LastBid.setText(MaxBid.toString())
                updateTimeFirebase(BidTime)
                updateMaxBidFirebase(MaxBid)
                updateLastBidderID()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //val rooView = view
        viewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        viewModel.allItems.observe(viewLifecycleOwner) {
            if (it != null) {
                it.forEach {
                    if (it._id.equals(item._id)) {
                        item = it
                        //val TimeNow = Timestamp(Date()).toDate()
                        val TimeNow = Date()
                        val BidTime =item._last_bid_time
                        if (BidTime == null || it._lastBid == 0)
                            time_in_milli_seconds = START_MILLI_SECONDS
                        else {
                            val diff: Long = TimeNow.getTime() - BidTime.getTime()
                            time_in_milli_seconds = START_MILLI_SECONDS - diff
                            if (time_in_milli_seconds < 0) {
                                Log.i("ItemViewBidFragment.kt", "Warning! Illegal time")
                            }
                        }
                        countdown_timer.cancel()
                        startTimer(time_in_milli_seconds)
                        if (item._lastBid >= MaxBid) {
                            MaxBid = item._lastBid
                            LastBid.setText(MaxBid.toString())
                        }
                    }
                }
            } else {
                Log.e("ItemViewBidFragment.kt", "-W- Null Item !!")
            }
        }

        BidBtn.setOnClickListener {
            checkInput()
        }
    }
}