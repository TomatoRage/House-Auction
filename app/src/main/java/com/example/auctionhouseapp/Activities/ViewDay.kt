package com.example.auctionhouseapp.Activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.auctionhouseapp.AuctionDayStatus
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.UserType
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.Timestamp
import java.util.*

class ViewDay : AppCompatActivity() {

    lateinit var Day: AuctionDays

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_day)

        Day = intent.getSerializableExtra("Day") as AuctionDays

        findViewById<TextView>(R.id.textview_day_title).setText(Day.Title)
        findViewById<TextView>(R.id.textView_start_date).setText("Start Date: " + Day.PrintDate())
        findViewById<TextView>(R.id.textView_start_time).setText("Start Time: " + Day.PrintStartTime())
        findViewById<TextView>(R.id.textView_sales_commission).setText("Commission: " + (Day.Commission*100).toInt().toString()+"%")
        if(Day.Status == AuctionDayStatus.Pending)
            findViewById<TextView>(R.id.textView_status).setText("Status: Pending")
        if(Day.Status == AuctionDayStatus.Occurred)
            findViewById<TextView>(R.id.textView_status).setText("Status: Occurred")
        if(Day.Status == AuctionDayStatus.Happening)
            findViewById<TextView>(R.id.textView_status).setText("Status: Happening")
        findViewById<TextView>(R.id.textview_total_earnings).setText("Earnings: " + Day.Earnings.toString()+"₪")
        findViewById<TextView>(R.id.textView_participants).setText("Participants: " + Day.ParticipantsNum.toString())
        findViewById<TextView>(R.id.textView_requested_items).setText("Requested: " + Day.NumOfRequested.toString())
        findViewById<TextView>(R.id.textview_num_of_items).setText("Items: " + Day.NumOfItems.toString())

        findViewById<Button>(R.id.btn_listed_items).setOnClickListener {
            val intent = Intent(applicationContext, ItemsList::class.java)
            intent.putExtra("Type",UserType.AuctionHouse.Type)
            intent.putExtra("Day",Day)
            intent.putExtra("House ID",FirebaseUtils.firebaseUser!!.uid)
            startActivity(intent)
            finish()
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
                .document(Day.DocumentID).delete()
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