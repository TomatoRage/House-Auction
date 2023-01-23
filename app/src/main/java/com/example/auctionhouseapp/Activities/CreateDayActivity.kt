package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class CreateDayActivity : AppCompatActivity() {

    private lateinit var DayTitle:EditText
    private lateinit var AuctionDay:EditText
    private lateinit var AuctionMonth:EditText
    private lateinit var AuctionYear:EditText
    private lateinit var AuctionHour:EditText
    private lateinit var AuctionMin:EditText
    private lateinit var Commission:EditText
    private lateinit var Lock:EditText
    private lateinit var FinishedDay:AuctionDays

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_day)

        DayTitle = findViewById<EditText>(R.id.day_title)
        AuctionDay = findViewById<EditText>(R.id.edittext_day)
        AuctionMonth = findViewById<EditText>(R.id.edittext_month)
        AuctionYear = findViewById<EditText>(R.id.edittext_year)
        AuctionHour = findViewById<EditText>(R.id.edittext_hour)
        AuctionMin = findViewById<EditText>(R.id.edittext_minute)
        Commission = findViewById<EditText>(R.id.edittext_commission)
        Lock = findViewById<EditText>(R.id.edittext_lock)

        textAutoCheck()

        findViewById<Button>(R.id.btn_back_create_day).setOnClickListener{
            val intent = Intent(applicationContext, HouseActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btn_create_day_confirm).setOnClickListener{
            checkInput()
        }
    }

    private fun textAutoCheck() {

        DayTitle.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (DayTitle.text.isEmpty()){
                    DayTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (DayTitle.text.length >= 4){
                    DayTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                DayTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count >= 4){
                    DayTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        AuctionDay.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (AuctionDay.text.isEmpty()){
                    AuctionDay.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (AuctionDay.text.length <= 2) {
                    AuctionDay.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                AuctionDay.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (AuctionDay.text.length <= 2) {
                    AuctionDay.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        AuctionMonth.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (AuctionMonth.text.isEmpty()){
                    AuctionMonth.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (AuctionMonth.text.length <= 2){
                    AuctionMonth.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                AuctionMonth.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (AuctionMonth.text.length <= 2) {
                    AuctionMonth.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

            AuctionYear.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (AuctionYear.text.isEmpty()){
                    AuctionYear.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (AuctionYear.text.length == 4){
                    AuctionYear.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                AuctionYear.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (AuctionYear.text.length == 4){
                    AuctionYear.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        AuctionHour.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (AuctionHour.text.isEmpty()){
                    AuctionHour.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (AuctionHour.text.length <= 2){
                    AuctionHour.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                AuctionHour.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count <= 2){
                    AuctionHour.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        AuctionMin.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (AuctionMin.text.isEmpty()){
                    AuctionMin.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (AuctionMin.text.length <= 2){
                    AuctionMin.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                AuctionMin.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count <= 2){
                    AuctionMin.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        Commission.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (AuctionMin.text.isEmpty()){
                    AuctionMin.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (Commission.text.length <= 2){
                    Commission.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                Commission.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count <= 2){
                    Commission.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        Lock.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (Lock.text.isEmpty()){
                    Lock.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (Lock.text.length <= 3){
                    Lock.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                Lock.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count <= 3){
                    Lock.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkInput(){

        if(DayTitle.text.isEmpty()) {
            toast("Title can't be empty!")
            return
        }
        if(AuctionDay.text.isEmpty() || AuctionMonth.text.isEmpty() || AuctionYear.text.isEmpty()) {
            toast("Start date can't be empty!")
            return
        }
        if(AuctionHour.text.isEmpty() || AuctionMin.text.isEmpty()) {
            toast("Start time can't be empty!")
            return
        }
        if(Commission.text.isEmpty()) {
            toast("Commission can't be empty!")
            return
        }
        if(Lock.text.isEmpty()) {
            toast("Participation lock time can't be empty!")
            return
        }
        if(AuctionDay.text.toString().toInt() < 0 || AuctionMonth.text.toString().toInt() < 0 ||
            AuctionYear.text.toString().toInt() < 0 || AuctionHour.text.toString().toInt() < 0 ||
            AuctionMin.text.toString().toInt() < 0  || AuctionDay.text.toString().toInt() > 31 ||
            AuctionMonth.text.toString().toInt() > 12 || AuctionYear.text.count() != 4 ||
            AuctionHour.text.toString().toInt() > 24 || AuctionMin.text.toString().toInt() > 60){
            toast("Start date and time must be a valid number")
            return
        }
        val Currentdate:LocalDateTime = LocalDateTime.now()
        val currentDate:Calendar = Calendar.getInstance()
        val inputedDate:Calendar = Calendar.getInstance()

        inputedDate.set(Calendar.YEAR, AuctionYear.text.toString().toInt())
        inputedDate.set(Calendar.MONTH, AuctionMonth.text.toString().toInt())
        inputedDate.set(Calendar.DAY_OF_MONTH, AuctionDay.text.toString().toInt())
        inputedDate.set(Calendar.HOUR_OF_DAY, AuctionHour.text.toString().toInt())
        inputedDate.set(Calendar.MINUTE, AuctionMin.text.toString().toInt())

        currentDate.set(Calendar.YEAR, Currentdate.year)
        currentDate.set(Calendar.MONTH, Currentdate.monthValue)
        currentDate.set(Calendar.DAY_OF_MONTH, Currentdate.dayOfMonth+2)
        currentDate.set(Calendar.HOUR_OF_DAY, Currentdate.hour)
        currentDate.set(Calendar.MINUTE, Currentdate.minute)

        if(inputedDate.before(currentDate)){
            toast("Start date and time must be a valid number")
            return
        }
        if(Commission.text.toString().toInt() < 0){
            toast("Commission must be a valid number")
            return
        }
        if(Lock.text.toString().toInt() < 0){
            toast("Participation lock time must be a valid number")
            return
        }

        inputedDate.set(Calendar.MONTH, AuctionMonth.text.toString().toInt()-1)

        StoreData(inputedDate.time)
    }


    fun UpdateNextSalesDay() {
        FirebaseUtils.houseCollectionRef.document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(Constants.SALES_DAY_COLLECTION)
            .orderBy(Constants.DAY_START_DATE, Query.Direction.ASCENDING).limit(1).get()
            .addOnSuccessListener { documents ->
                for(doc in documents) {
                    val Day = AuctionDays(doc.data)
                    FirebaseUtils.houseCollectionRef.document(FirebaseAuth.getInstance().currentUser!!.uid).update(
                        mapOf(
                            Constants.HOUSE_NEXT_SALES_DATE to Day.StartDate,
                        )
                    ).addOnSuccessListener {
                        val NextSalesDate = SimpleDateFormat("dd/MM/yyyy").format(Day.StartDate).toString()
                        toast("Next Sales Date is: $NextSalesDate")
                    }.addOnFailureListener {
                        toast("Failed to update Next Sales Day!")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("CreateDayActivity ", "Failed to update Next Sales Day!!", exception)
            }
    }

    fun StoreData(inputDate:Date){

        val Day = AuctionDays()

        Day.Title = DayTitle.text.toString()
        Day.StartDate = inputDate
        Day.Commission = (Commission.text.toString().toDouble())/100
        Day.LockBefore = Lock.text.toString().toInt()
        Day.ParticipantsNum = 0
        Day.Earnings = 0
        Day.NumOfItems = 0
        Day.NumOfRequested = 0
        Day.NumOfSoldItems = 0

        Day.StoreData(FirebaseAuth.getInstance().currentUser!!.uid,::OnSuccPerform)

    }

    fun OnSuccPerform() {
        UpdateNextSalesDay()
        val intent = Intent(applicationContext, HouseActivity::class.java)
        setResult(RESULT_OK,intent)
        finish()
    }
}