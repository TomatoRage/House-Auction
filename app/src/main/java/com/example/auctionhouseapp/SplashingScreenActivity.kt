package com.example.auctionhouseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.auctionhouseapp.Utils.FirebaseUtils.firebaseUser
import com.example.auctionhouseapp.Utils.FirebaseUtils.userCollectionRef
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import java.util.*


class SplashingScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        Handler().postDelayed({

            checkUser()

        }, 1000)

    }

    private fun checkUser() = CoroutineScope(Dispatchers.IO).launch {

        if(firebaseUser != null){
            try {
                val querySnapshot = userCollectionRef
                    .document(firebaseUser.uid)
                    .get().await()

                val userType:Int =  querySnapshot.data?.get(Constants.USER_TYPE).toString().toInt()
                if (userType == 0) {
                    val intent = Intent(applicationContext, CustomerActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            } catch (e:Exception) {
                Log.i("SplashScreenActivity","-E- WHILE RUNNING CHECK USER FUNCTION")
            }
        }
        if(firebaseUser == null){
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}