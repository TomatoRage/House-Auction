package com.example.auctionhouseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
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

        }, 1000)

    }

    private fun checkUser(){

        if(firebaseUser != null){
            val querySnapshot = userCollectionRef
                .document(firebaseUser.uid)
                .get().addOnSuccessListener{ doc ->
                    if(doc != null){
                        val userType:Int =  (doc.data!![Constants.USER_TYPE] as Long).toInt()
                        if (userType == 0) {
                            val intent = Intent(applicationContext, CustomerActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(applicationContext, CustomerActivity::class.java) // TODO: UPDATE ACTIVITY
                            startActivity(intent)
                            finish()
                        }
                    }else {
                        Log.d(TAG,"DOCUMENT NOT FOUND")
                    }
                }
        }
        if(firebaseUser == null){
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private val TAG = "SplashActivity"
    }
}