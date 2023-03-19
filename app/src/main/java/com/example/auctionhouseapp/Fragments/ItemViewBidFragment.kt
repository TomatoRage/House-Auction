package com.example.auctionhouseapp.Fragments

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.Objects.ItemViewModel
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import nl.dionsegijn.konfetti.KonfettiView


class ItemViewBidFragment : Fragment() {
    lateinit var item: Item
    lateinit var imageView: ImageView
    lateinit var NextBtn: ImageButton
    lateinit var PrevBtn: ImageButton
    lateinit var BidBtn: Button
    lateinit var EditBid: EditText
    lateinit var RemainingTime: TextView
    lateinit var LastBid:TextView
    lateinit var viewKonfetti:KonfettiView
    private var position = 0
    private  lateinit var viewModel: ItemViewModel
    var MaxBid:Int = 0
    val currentCustomer = FirebaseAuth.getInstance().uid.toString()
    var START_MILLI_SECONDS = 60000L
    lateinit var countdown_timer: CountDownTimer
    var time_in_milli_seconds = 0L



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_view_item_bid, container, false)
        NextBtn = view.findViewById<ImageButton>(R.id.btn_next_img)
        imageView = view.findViewById<ImageView>(R.id.img_item)
        PrevBtn = view.findViewById<ImageButton>(R.id.btn_prev_img)
        BidBtn = view.findViewById<Button>(R.id.btn_bid)
        LastBid = view.findViewById<TextView>(R.id.txt_last_bid)
        EditBid = view.findViewById<EditText>(R.id.edit_txt_bid)
        RemainingTime = view.findViewById<TextView>(R.id.txt_remaining_time)
        viewKonfetti = view.findViewById(R.id.viewKonfetti)

        Glide.with(this)
            .load(item._imagesUrls.get(position))
            .into(imageView)
        view.findViewById<TextView>(R.id.item_name).setText(item._name)
        view.findViewById<TextView>(R.id.item_description).setText(item._description)
        view.findViewById<TextView>(R.id.txt_last_bid).setText(MaxBid.toString())

        //Count Down Object
        time_in_milli_seconds = START_MILLI_SECONDS
        startTimer(time_in_milli_seconds)


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
        MaxBid = item._lastBid
        return view
    }


    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                loadConfeti()
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTimeUI()
            }
        }
        countdown_timer.start()
    }

    private fun resetTimer() {
        countdown_timer.cancel()
        startTimer(START_MILLI_SECONDS)
    }

    private fun updateTimeUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        RemainingTime.text = "$minute:$seconds"
        val time_in_mili_seconds_updated = minute*60*1000 + seconds*1000
        if (currentCustomer.equals(item._lastBidderId) && seconds.toInt()%5==0)
            updateTimeFirebase(time_in_mili_seconds_updated)
    }

    private fun updateTimeFirebase(time_in_mili_seconds_updated: Long) {
        FirebaseUtils.itemsCollectionRef
            .document(item._id)
            .update(Constants.ITEM_TIME_FOR_AUCTION_END, time_in_mili_seconds_updated.toLong())
            .addOnSuccessListener {
                Log.i("ItemViewBidFragment.kt", "update remaining time in milli seconds to"
                        + time_in_mili_seconds_updated)
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Failed to update remaining time !!",
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //val rooView = view
        viewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        viewModel.allItems.observe(viewLifecycleOwner) {
            if (it != null) {
                it.forEach {
                    if (it._id.equals(item._id)) {
                        item = it
                        countdown_timer.cancel()
                        startTimer(it._time_for_auction_end.toLong())
                        if (item._lastBid > MaxBid) {
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
            val bid = EditBid.text.toString()
            if (bid.isEmpty()) {
                Toast.makeText(context, "Insert Valid Bid", Toast.LENGTH_SHORT).show()
            } else {
                val bidAmount = bid.toInt()
                if (bidAmount > MaxBid) {
                    resetTimer()
                    item._lastBid = bidAmount
                    item._time_for_auction_end = START_MILLI_SECONDS.toInt()
                    MaxBid = bidAmount
                    LastBid.setText(MaxBid.toString())
                    updateMaxBidFirebase(MaxBid)
                    updateTimeFirebase(START_MILLI_SECONDS)
                    updateLastBidderID()
                }
            }
        }
    }
}