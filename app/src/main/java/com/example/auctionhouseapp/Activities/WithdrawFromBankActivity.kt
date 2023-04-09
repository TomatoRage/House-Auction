package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth

class WithdrawFromBankActivity : AppCompatActivity() {
    private lateinit var btnWithdraw:Button
    private lateinit var editAmount: EditText
    private val pattern = """\d+"""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_from_bank)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        btnWithdraw = findViewById<Button>(R.id.btn_withdraw)
        editAmount = findViewById<EditText>(R.id.edit_txt_withdraw)

        btnWithdraw.setOnClickListener {
            checkInput()
        }

        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            val intent = Intent(this, CustomerMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkInput() {
        if (editAmount.text.isEmpty()) {
            Toast.makeText(this, "Empty Amount !", Toast.LENGTH_SHORT).show()
            return
        }
        if (!editAmount.text.matches(pattern.toRegex())) {
            Toast.makeText(this, "Enter Valid Amount Please", Toast.LENGTH_SHORT).show()
            return
        }
        withdraw()
    }

    private fun withdraw() {
        val currentCustomer = FirebaseAuth.getInstance().uid.toString()
        val withdrawAmount = editAmount.text.toString().toInt()
        FirebaseUtils.customerCollectionRef
            .document(currentCustomer)
            .get()
            .addOnSuccessListener {
                val customer = Customer(it.data)
                val cash = customer.getCash()
                FirebaseUtils.customerCollectionRef
                    .document(currentCustomer)
                    .update(Constants.USER_CASH,cash + withdrawAmount)
                    .addOnSuccessListener {
                        toast("Transferring Cash To Your Account...")
                    }
                    .addOnFailureListener {
                        Log.i("WithdrawFromBank.kt", "Failed to withdraw money")
                    }

            }.addOnFailureListener {
                Log.i("WithdrawFromBank.kt", "Failed get cash for withdrawing")
            }
    }

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent(applicationContext, CustomerMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}