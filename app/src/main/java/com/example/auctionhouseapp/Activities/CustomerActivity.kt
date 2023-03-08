package com.example.auctionhouseapp.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.AuctionHousesListFragment
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.FirebaseUtils.houseCollectionRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CustomerActivity : AppCompatActivity() {
    val List = AuctionHousesListFragment()
    val LoadingFragment = AuctionDaysSpinner()
    val HousesList: ArrayList<AuctionHouse> = arrayListOf()
    var counter :Int = 0
    var numOfHouses:Int = 0
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)


        //fitch all auction houses from firebase and add them to HousesList array
        FetchHousesData(::PerformAfterDataFetch)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewAuctionHouses, LoadingFragment)
            commit()
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentContainerViewAuctionHouses,LoadingFragment)
                    commit()
                }
            }
        }

        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun FetchHousesData (PerformAfterData:()->Unit) {
        houseCollectionRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    numOfHouses = documents.size()
                    for (doc in documents) {
                        val House: AuctionHouse = AuctionHouse()
                        House.FetchHouseData(doc.id, ::toPerform)
                        HousesList.add(House)
                    }
                } else {
                    Log.i("CustomerActivity", "-E- while fetching auction houses")
                }
            }
    }

    fun PerformAfterDataFetch() {
        var HousesListFiltered: ArrayList<AuctionHouse> = arrayListOf()
        for (house in HousesList) {
            if (!house.Days.isEmpty())
                HousesListFiltered.add(house)
        }
        List.HousesList = HousesListFiltered
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerViewAuctionHouses, List)
            commit()
        }
    }


    fun toPerform() {
        this.counter+=1
        if (counter == numOfHouses) {
            PerformAfterDataFetch()
        }
    }
}
