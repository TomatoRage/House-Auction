package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth


class SplashingScreenActivity : AppCompatActivity() {
    var userType = -1
    val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        Handler().postDelayed({
            checkUser(::goToNextActivity)

        }, 300)

    }

    fun checkUser(ToPerform: () -> Unit) {
        if(currentUser != null) {
            currentUser.uid.let {
                FirebaseUtils.usersCollectionRef
                    .document(it)
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc != null) {
                            userType = (doc.data?.get(Constants.USER_TYPE) as Long).toInt()
                            if (userType != -1)
                                ToPerform()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("LoginActivity", "Requested Items data read failed with", exception)
                    }
            }
        }
       if(FirebaseUtils.firebaseUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun goToNextActivity() {
        if (userType == 0) {
            val intent = Intent(applicationContext, CustomerMainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (userType == 1) {
            val intent = Intent(applicationContext, HouseActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            toast("Error While Reading User Type!")
        }
    }

    companion object {
        private val TAG = "SplashActivity"
    }
}