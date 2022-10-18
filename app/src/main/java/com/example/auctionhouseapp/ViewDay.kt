package com.example.auctionhouseapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp
import java.util.*

class ViewDay : AppCompatActivity() {

    lateinit var AuctionDay:AuctionDays
    var Title:String = String()
    var Date:Date = Date()
    var Commision:Double = 0.0
    var LockTime:Long = -1
    var Participation:Long = -1
    var Earnings:Long = -1
    var Items:Long = -1
    var Requested:Long = -1
    var Sold:Long = -1
    var ID:String = String()

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_day)

        Title = intent.getStringExtra("Day Title")!!
        Date.time = intent.getLongExtra("Start Date",0)
        Commision = intent.getDoubleExtra("Commission", 0.0)
        LockTime = intent.getLongExtra("Lock Time",0)
        Participation = intent.getLongExtra("Participation",0)
        Earnings = intent.getLongExtra("Earnings",0)
        Items = intent.getLongExtra("Items",0)
        Requested = intent.getLongExtra("Requested",0)
        Sold = intent.getLongExtra("Sold",0)
        ID = intent.getStringExtra("Document ID")!!

        AuctionDay = AuctionDays( hashMapOf<String,Any>(
            Constants.DAY_NAME to Title,
            Constants.DAY_START_DATE to Timestamp(Date),
            Constants.DAY_COMMISSION to Commision,
            Constants.DAY_LOCK_TIME to LockTime,
            Constants.DAY_NUM_OF_PARTICIPANTS to Participation,
            Constants.DAY_EARNINGS to Earnings,
            Constants.DAY_NUM_OF_ITEMS to Items,
            Constants.DAY_NUM_OF_REQUESTED to Requested,
            Constants.DAY_NUM_OF_SOLD to Sold))

        AuctionDay.DocumentID = ID

        findViewById<TextView>(R.id.textview_day_title).setText(AuctionDay.Title)
        findViewById<TextView>(R.id.textView_start_date).setText("Start Date: " + AuctionDay.PrintDate())
        findViewById<TextView>(R.id.textView_start_time).setText("Start Time: " + AuctionDay.PrintStartTime())
        findViewById<TextView>(R.id.textView_sales_commission).setText("Commission: " + (AuctionDay.Commission*100).toInt().toString()+"%")
        if(AuctionDay.Status == AuctionDayStatus.Pending)
            findViewById<TextView>(R.id.textView_status).setText("Status: Pending")
        if(AuctionDay.Status == AuctionDayStatus.Occurred)
            findViewById<TextView>(R.id.textView_status).setText("Status: Occurred")
        if(AuctionDay.Status == AuctionDayStatus.Happening)
            findViewById<TextView>(R.id.textView_status).setText("Status: Happening")
        findViewById<TextView>(R.id.textview_total_earnings).setText("Earnings: " + AuctionDay.Earnings.toString()+"₪")
        findViewById<TextView>(R.id.textView_participants).setText("Participants: " + AuctionDay.ParticipantsNum.toString())
        findViewById<TextView>(R.id.textView_requested_items).setText("Requested: " + AuctionDay.NumOfRequested.toString())
        findViewById<TextView>(R.id.textview_num_of_items).setText("Items: " + AuctionDay.NumOfItems.toString())

        findViewById<Button>(R.id.btn_listed_items).setOnClickListener {
            //TODO: Fill in functionality
        }
        findViewById<Button>(R.id.btn_requested_items).setOnClickListener {
            //TODO: Fill in functionality
        }
        findViewById<Button>(R.id.btn_delete_day).setOnClickListener {
            DeleteDay()
        }
        findViewById<Button>(R.id.btn_back).setOnClickListener {
            val intent = Intent(applicationContext, HouseActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**Delete Day Document and update next sales day**/

    fun DeleteDay(){

        var Today:Timestamp = Timestamp(Date())
        var builder = AlertDialog.Builder(this)

        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete this day?")
        builder.setPositiveButton("Confirm",DialogInterface.OnClickListener{dialog,id ->
            /** Delete Document **/
            FirebaseUtils.houseCollectionRef
                .document(FirebaseUtils.firebaseUser!!.uid)
                .collection(Constants.SALES_DAY_COLLECTION)
                .document(ID).delete()
                .addOnSuccessListener {
                    /** Get Next Sales Date from days collection**/
                    FirebaseUtils.houseCollectionRef
                        .document(FirebaseUtils.firebaseAuth.currentUser!!.uid)
                        .collection(Constants.SALES_DAY_COLLECTION)
                        .whereGreaterThan(Constants.DAY_START_DATE,Today).limit(1)
                        .get()
                        .addOnSuccessListener{ docs ->

                            var NextDate = docs.documents[0].data!![Constants.DAY_START_DATE] as Timestamp

                            /**Store the next sales day in house document**/
                            FirebaseUtils.houseCollectionRef
                                .document(FirebaseUtils.firebaseUser!!.uid)
                                .update(Constants.HOUSE_NEXT_SALES_DATE, NextDate)
                                .addOnSuccessListener {
                                    dialog.cancel()
                                    val intent = Intent(applicationContext, HouseActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "house data write failed with", exception)
                                }

                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "day data read failed with", exception)
                        }

                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "day data delete failed with", exception)
                }
        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        var alert = builder.create()
        alert.show()
    }

    companion object {
        private val TAG = "View Day"
    }

}