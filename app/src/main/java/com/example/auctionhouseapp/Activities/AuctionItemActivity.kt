package com.example.auctionhouseapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageSwitcher
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils
import java.util.*

class AuctionItemActivity : AppCompatActivity() {

    private lateinit var edit_item_name:EditText
    private lateinit var edit_item_description:EditText
    private lateinit var edit_starting_price:EditText
    private lateinit var image_switcher:ImageSwitcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auction_item)

        /*TODO fitch item from firebase*/
        /**
         *
         *
         *
         *
         */
        edit_item_name = findViewById<EditText>(R.id.edit_txt_name)
        edit_item_description = findViewById<EditText>(R.id.edit_txt_description)
        edit_starting_price = findViewById<EditText>(R.id.edit_txt_starting_price)
        image_switcher = findViewById<ImageSwitcher>(R.id.img_switcher1)


        findViewById<Button>(R.id.btn_auction_item2).setOnClickListener{
            checkInput()
        }

        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.txt_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_pick_item).setOnClickListener{
            /*TODO UPLOAD IMAGES FUN*/
        }

        textAutoCheck()
    }

    private fun textAutoCheck() {
        edit_item_name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (edit_item_name.text.isEmpty()){
                    edit_item_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (edit_item_name.text.length <= 1) {
                    edit_item_name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                edit_item_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (edit_item_name.text.length <= 1) {
                    edit_item_name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        edit_item_description.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (edit_item_description.text.isEmpty()){
                    edit_item_description.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (edit_item_description.text.length <= 3) {
                    edit_item_description.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                edit_item_description.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (edit_item_description.text.length <= 3) {
                    edit_item_description.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        edit_starting_price.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (edit_starting_price.text.isEmpty()){
                    edit_starting_price.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (edit_starting_price.text.length >= 7) {
                    edit_starting_price.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                edit_starting_price.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (edit_starting_price.text.length >= 7) {
                    edit_starting_price.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })
    }

    fun checkInput() {
        if(edit_item_name.text.isEmpty()) {
            toast("Item's name can't be empty!")
            return
        }

        if(edit_item_description.text.isEmpty()) {
            toast("Item's description can't be empty!")
            return
        }

        if(edit_starting_price.text.isEmpty()) {
            toast("Item's starting price can't be empty!")
            return
        }
        if ( edit_starting_price.text.toString().toInt() < 0) {
            toast("Invalid Item's starting price")
            return
        }


    }

    fun StoreData(inputDate: Date){
    /*TODO ALL LINE IN COMMENT*/
        val item = Item()

        item.ownerId = FirebaseUtils.firebaseUser?.uid.toString()
        item.Name = edit_item_name.text.toString()
        item.Description = edit_item_description.text.toString()
        //item.docID = ??
        //item.ImagesArray = ??
        item.startingPrice = edit_starting_price.text.toString().toInt()
        item.lastBid = -1
        item.lastBidderId = null

       // item.StoreData(FirebaseUtils.firebaseAuth.currentUser!!.uid,::OnSuccPerform)

    }
}