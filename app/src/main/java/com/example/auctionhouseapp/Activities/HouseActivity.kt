package com.example.auctionhouseapp.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.*
import com.example.auctionhouseapp.Fragments.AuctionDaysListFragment
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HouseActivity : AppCompatActivity() {

    val House: AuctionHouse = AuctionHouse()
    val LoadingFragment = AuctionDaysSpinner()
    val List = AuctionDaysListFragment()
    lateinit var resultLauncher:ActivityResultLauncher<Intent>

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auctionhouse)

        House.FetchHouseData(Firebase.auth.currentUser!!.uid,::PerformAfterData)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,LoadingFragment)
            commit()
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainerView,LoadingFragment)
                    commit()
                }
                House.Days.clear()
                House.FetchHouseData(Firebase.auth.currentUser!!.uid,::PerformAfterData)

            }
        }

        findViewById<Button>(R.id.btn_add_day).setOnClickListener {

            val intent = Intent(applicationContext, CreateDayActivity::class.java)
            intent.putExtra("House", House.GetUID())
            resultLauncher.launch(intent)
        }

        findViewById<Button>(R.id.btn_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun PerformAfterData() {

        if(supportFragmentManager.isDestroyed)
            return

        List.House = House

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView,List)
            commit()
        }

    }
}