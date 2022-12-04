package com.example.auctionhouseapp.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageSwitcher
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.example.auctionhouseapp.Utils.FirebaseUtils.firebaseStore
import com.example.auctionhouseapp.Utils.FirebaseUtils.storageReference
import com.example.auctionhouseapp.databinding.ActivityAuctionItemBinding
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class AuctionItemActivity : AppCompatActivity() {

    private lateinit var edit_item_name:EditText
    private lateinit var edit_item_description:EditText
    private lateinit var edit_starting_price:EditText
    private lateinit var image_switcher:ImageSwitcher
    private lateinit var houseId:String
    private lateinit var dayId:String
    lateinit var ImagesArray:ArrayList<Bitmap>
    lateinit var binding : ActivityAuctionItemBinding
    lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auction_item)
        houseId = intent.getStringExtra("House ID")!!
        dayId = intent.getStringExtra("Day ID")!!

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

        binding.btnPickItem.setOnClickListener{
            /*TODO UPLOAD IMAGES AS BITMAP, ADD IMAGE TO ARRAY, SAVE IN FIRESTORE*/
            selectImage()

        }

        textAutoCheck()
    }

    fun selectImage() {
        val intent = Intent()
        intent.type = "Items/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            binding.imgSwitcher1.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageRef = firebaseStore.getReference("Items/$fileName")

        storageRef.putFile(imageUri).
                addOnSuccessListener {

                    binding.imgSwitcher1.setImageURI(null)
                    Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                    if(progressDialog.isShowing) progressDialog.dismiss()
                }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

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
            toast("Invalid Item's starting price!")
            return
        }

        if (StoreData() < 0) {
            toast("Must upload image!")
            return
        }
    }

    fun StoreData():Int {
        val item = Item()
        item.ownerId = FirebaseUtils.firebaseUser?.uid.toString()
        item.Name = edit_item_name.text.toString()
        item.Description = edit_item_description.text.toString()
        if (ImagesArray.isEmpty()) {
            return -1
        }
        item.ImagesArray = ImagesArray
        item.startingPrice = edit_starting_price.text.toString().toInt()
        item.lastBid = -1
        item.lastBidderId = null
        item.StoreData(houseId, dayId,::OnSuccPerform)
        return 0
    }

    fun OnSuccPerform() {
        val intent = Intent(applicationContext, ItemsList::class.java)
        setResult(RESULT_OK,intent)
        finish()
    }
}