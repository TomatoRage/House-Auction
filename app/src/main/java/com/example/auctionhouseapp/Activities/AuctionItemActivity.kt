package com.example.auctionhouseapp.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.auctionhouseapp.AuctionDays
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.Objects.Customer
import com.example.auctionhouseapp.Objects.Item
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.example.auctionhouseapp.Utils.FirebaseUtils.firebaseStore
import com.example.auctionhouseapp.Utils.FirebaseUtils.storageReference
import com.example.auctionhouseapp.databinding.ActivityAuctionItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlin.collections.ArrayList

class AuctionItemActivity : AppCompatActivity() {

    private lateinit var edit_item_name:EditText
    private lateinit var edit_item_description:EditText
    private lateinit var edit_starting_price:EditText
    private lateinit var imageSwitcher:ImageSwitcher
    private var house = AuctionHouse()
    private lateinit var dayId:String
    private var position = 0
    lateinit var ImagesUri:ArrayList<Uri>
    lateinit var ImagesIDs:ArrayList<String>
    lateinit var binding : ActivityAuctionItemBinding
    lateinit var NextBtn: ImageButton
    lateinit var PrevBtn: ImageButton
    private var Commission:Double = 0.1
    private var itemOwner:Customer = Customer()
    //init item
    var item = Item()
    var uploadedImages = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageSwitcher = findViewById<ImageSwitcher>(R.id.img_switcher1)
        imageSwitcher.setFactory { ImageView(applicationContext) }
        house.SetID(intent.getStringExtra("House ID")!!)
        dayId = intent.getStringExtra("Day ID")!!
        Commission = intent.getDoubleExtra("Commission",Commission)
        edit_item_name = findViewById<EditText>(R.id.edit_item_name)
        edit_item_description = findViewById<EditText>(R.id.edit_item_description)
        edit_starting_price = findViewById<EditText>(R.id.edit_txt_starting_price)
        NextBtn = findViewById<ImageButton>(R.id.btn_next_img)
        PrevBtn = findViewById<ImageButton>(R.id.btn_prev_img)
        ImagesUri = ArrayList()
        ImagesIDs = ArrayList()
        item._imagesUrls = arrayListOf()
        fetchItemOwner()
        findViewById<Button>(R.id._btn_auction_item).setOnClickListener{
            checkInput()
        }

        findViewById<TextView>(R.id.txt_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<ImageView>(R.id.ic_back).setOnClickListener {
            finish()
        }

        binding.btnPickItem.setOnClickListener{
            /*TODO UPLOAD IMAGES AS BITMAP, ADD IMAGE TO ARRAY, SAVE IN FIRESTORE*/
            selectImage()

        }

        textAutoCheck()

        NextBtn.setOnClickListener {
            if (position < ImagesUri.size - 1) {
                position++
                val imageUri = ImagesUri.get(position)
                imageSwitcher.setImageURI(imageUri)

            } else {
                toast("No More Images...")
            }
        }

        PrevBtn.setOnClickListener {
            if (position > 0) {
                position--
                val imageUri = ImagesUri.get(position)
                imageSwitcher.setImageURI(imageUri)
            } else {
                toast("No More Images...")
            }
        }
    }

    private fun fetchItemOwner() {
        val itemOwnerId = FirebaseAuth.getInstance().uid.toString()
        FirebaseUtils.customerCollectionRef
            .document(itemOwnerId)
            .get()
            .addOnSuccessListener {
                itemOwner = Customer(it.data)
            }
            .addOnFailureListener {
                Log.i("ItemViewBidFragment.kt", "Failed to update winner name")
            }
    }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra(Intent.ACTION_GET_CONTENT, true)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data!!.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    //add image to list
                    ImagesUri!!.add(imageUri)
                    position = 0
                }
                binding.imgSwitcher1.setImageURI(ImagesUri[0])
            } else {
                val imageUri = data?.data!!
                binding.imgSwitcher1.setImageURI(imageUri)
                position = 0
            }
        }
    }

    private fun uploadImages() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        uploadedImages = 0
        for (i in 0 until ImagesUri.size) {
            var fileName = ImagesUri.get(i).toString()
            fileName = fileName.split("/").last()
            ImagesIDs.add(fileName)
            val imageUri = ImagesUri.get(i)
            val storageRef = firebaseStore.getReference("Items/$fileName")
            storageRef.putFile(imageUri).
            addOnSuccessListener {
                binding.imgSwitcher1.setImageURI(null)
                storageRef.downloadUrl.addOnCompleteListener {
                    item._imagesUrls.add(it.result.toString())
                    uploadedImages++
                    checkAllImagesUpload()
                }
                //if(progressDialog.isShowing) progressDialog.dismiss()
            }.addOnFailureListener {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkAllImagesUpload() {
        if (uploadedImages == ImagesUri.size) {
            item._imagesIDs = ImagesIDs
            storeItem()
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
        CreateItem()
    }

    fun CreateItem() {
        house.FetchHousePrimaryData(house.GetUID(),::uploadImages)
    }

    fun storeItem() {
        item._ownerId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        item._name = edit_item_name.text.toString()
        item._description = edit_item_description.text.toString()
        item._startingPrice = edit_starting_price.text.toString().toInt()
        item._status = "Pending"
        item._auctionHouseName = house.GetName()
        item._ownerPhoneNumber = itemOwner.GetPhoneNumber()
        item._commission = Commission
        item.StoreData(
            Constants.REQUESTED_ITEMS,
            house.GetUID(),
            dayId,
            item._ownerId,
            ::OnSuccPerform
        )
    }

    fun OnSuccPerform() {
        finish()
    }

}