package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.AuctionHousesListFragment
import com.example.auctionhouseapp.Fragments.CustomerProfile
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class CustomerMainActivity : AppCompatActivity() {
    val List = AuctionHousesListFragment()
    val LoadingFragment = AuctionDaysSpinner()
    val HousesList: ArrayList<AuctionHouse> = arrayListOf()
    var numOfHousesFetched :Int = 0
    var numOfHouses:Int = 0
    lateinit var customerName:String
    lateinit var customerEmail:String
    private lateinit var navController: NavController
    private var customerProfile = CustomerProfile()

   // private val auctionHousesListFragment = AuctionHousesListFragment()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)
        //customerName = intent.getStringExtra("User Name") as String
        //customerEmail = intent.getStringExtra("User Email")  as String
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.customerMainContainer) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavCustomer)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.customerProfile -> {
                    //customerProfile.customerName = customerName
                    //customerProfile.customerEmail = customerEmail
                    replaceFragment(customerProfile)
                }
                R.id.auctionHousesListFragment -> {
                    //fitch all auction houses from firebase and add them to HousesList array
                    HousesList.clear()
                    FetchHousesData(::PerformAfterDataFetch)
                    replaceFragment(LoadingFragment)
                }
            }
            true
        }

       findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
           FirebaseAuth.getInstance().signOut()
           val intent = Intent(applicationContext, LoginActivity::class.java)
           startActivity(intent)
           finish()
       }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.customerMainContainer, fragment)
        transaction.commit()
    }

    fun FetchHousesData (PerformAfterData:()->Unit) {
        FirebaseUtils.houseCollectionRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    numOfHouses = documents.size()
                    for (doc in documents) {
                        val House: AuctionHouse = AuctionHouse()
                        House.FetchHousePrimaryData(doc.id, ::toPerform)
                        HousesList.add(House)
                    }
                    if (documents.isEmpty) {
                        PerformAfterDataFetch()
                    }
                } else {
                    Log.i("CustomerActivity", "-E- while fetching auction houses")
                }
            }
    }

    fun toPerform() {
        this.numOfHousesFetched+=1
        if (numOfHousesFetched == numOfHouses) {
            this.numOfHousesFetched = 0
            PerformAfterDataFetch()
        }
    }

    fun PerformAfterDataFetch() {
        var HousesListFiltered: ArrayList<AuctionHouse> = arrayListOf()
        for (house in HousesList) {
            if (house.NextSalesDay != null)//check null start day
                HousesListFiltered.add(house)
        }
        List.HousesList = HousesListFiltered
        replaceFragment(List)
    }
}