package com.example.auctionhouseapp.Fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.Objects.ItemViewModel
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import androidx.lifecycle.Observer
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
        EditBid = view.findViewById<EditText>(R.id.edit_txt_bid)
        RemainingTime = view.findViewById<TextView>(R.id.txt_remaining_time)
        viewKonfetti = view.findViewById(R.id.viewKonfetti)

        Glide.with(this)
            .load(item.imagesUrls.get(position))
            .into(imageView)
        view.findViewById<TextView>(R.id.item_name).setText(item.Name)
        view.findViewById<TextView>(R.id.item_description).setText(item.Description)
        view.findViewById<TextView>(R.id.txt_last_bid).setText(MaxBid.toString())

        //Count Down Object
        time_in_milli_seconds = START_MILLI_SECONDS
        startTimer(time_in_milli_seconds)


        NextBtn.setOnClickListener {
            if (position < item.imagesUrls.size - 1) {
                position++
                Glide.with(this)
                    .load(item.imagesUrls.get(position))
                    .into(imageView)

            } else {
                Toast.makeText(context, "No More Images...", Toast.LENGTH_SHORT).show()
            }
        }

        PrevBtn.setOnClickListener {
            if (position > 0) {
                position--

                Glide.with(this)
                    .load(item.imagesUrls.get(position))
                    .into(imageView)
            } else {
                Toast.makeText(context, "No More Images...", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }


    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                loadConfeti()
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()

    }

    private fun resetTimer() {
        countdown_timer.cancel()
        startTimer(START_MILLI_SECONDS)
    }

    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        RemainingTime.text = "$minute:$seconds"
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
        viewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
        viewModel.current_item.observe(viewLifecycleOwner) {
            if (it != null) {
                item = it
            }
            if (item.lastBid > MaxBid) {
                LastBid.setText(MaxBid.toString())
                MaxBid = item.lastBid
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
                    item.lastBid = bidAmount
                    FirebaseUtils.itemsCollectionRef
                        .document(item.ID)
                        .update(Constants.ITEM_LAST_BID_AMOUNT, bidAmount)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Highest Bid So Far !!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to update your bid !!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }


    }
}