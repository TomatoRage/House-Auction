package com.example.auctionhouseapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.auctionhouseapp.Fragments.AuctionDaysSpinner
import com.example.auctionhouseapp.Fragments.AuctionHousesListFragment
import com.example.auctionhouseapp.Fragments.CustomerItemsFragment
import com.example.auctionhouseapp.Fragments.CustomerProfile
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerMainActivity : AppCompatActivity() {
    val List = AuctionHousesListFragment()
    val LoadingFragment = AuctionDaysSpinner()
    val HousesList: ArrayList<AuctionHouse> = arrayListOf()
    var counter :Int = 0
    var numOfHouses:Int = 0
    private lateinit var navController: NavController
    private val customerProfile = CustomerProfile()
    private val customerItemsFragment = CustomerItemsFragment()
   // private val auctionHousesListFragment = AuctionHousesListFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.customerMainContainer) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavCustomer)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.customerProfile -> replaceFragment(customerProfile)
                R.id.customerItemsFragment -> replaceFragment(customerItemsFragment)
                R.id.auctionHousesListFragment -> {
                    //fitch all auction houses from firebase and add them to HousesList array
                    HousesList.clear()
                    FetchHousesData(::PerformAfterDataFetch)
                    replaceFragment(LoadingFragment)
                }
            }
            true
        }
        //setupWithNavController(bottomNavigationView, navController)

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
                        House.FetchHouseData(doc.id, ::toPerform)
                        HousesList.add(House)
                    }
                } else {
                    Log.i("CustomerActivity", "-E- while fetching auction houses")
                }
            }
    }

    fun toPerform() {
        this.counter+=1
        if (counter == numOfHouses) {
            this.counter = 0
            PerformAfterDataFetch()
        }
    }

    fun PerformAfterDataFetch() {
        var HousesListFiltered: ArrayList<AuctionHouse> = arrayListOf()
        for (house in HousesList) {
            if (!house.Days.isEmpty())
                HousesListFiltered.add(house)
        }
        List.HousesList = HousesListFiltered
        replaceFragment(List)
    }
}