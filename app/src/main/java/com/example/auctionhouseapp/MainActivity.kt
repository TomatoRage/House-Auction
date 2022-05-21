package com.example.auctionhouseapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.auctionhouseapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), FragmentNavigation{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.house,LoginFragment())
            .commit()
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