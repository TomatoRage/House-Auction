package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils.firebaseUser
import com.example.auctionhouseapp.Utils.FirebaseUtils.userCollectionRef


class SplashingScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        Handler().postDelayed({

            checkUser()

        }, 100)

    }

    private fun checkUser(){

        if(firebaseUser != null){
            firebaseUser.let {
                userCollectionRef
                    .document(it.uid)
                    .get().addOnSuccessListener{ doc ->
                        if(doc != null){
                            val userType:Int =  (doc.data!![Constants.USER_TYPE] as Long).toInt()
                            if (userType == 0) {
                                val intent = Intent(applicationContext, CustomerActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(applicationContext, HouseActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Log.d("SplashingActivity", "DOCUMENT NOT FOUND")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("SplashingActivity", "USER FAILURE",exception)
                    }
            }
        }
        if(firebaseUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private val TAG = "SplashActivity"
    }
}