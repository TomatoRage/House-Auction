package com.example.auctionhouseapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.auctionhouseapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), FragmentNavigation{
    private lateinit var fAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fAuth = Firebase.auth

        val currenUser = fAuth.currentUser
        if(currenUser != null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.house,AuctionDaysListFragment()).addToBackStack(null)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.house,LoginFragment())
                .commit()
        }
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.house,fragment)
        if(addToStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}