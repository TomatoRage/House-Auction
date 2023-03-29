package com.example.auctionhouseapp.Fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.auctionhouseapp.Activities.LoginActivity
import com.example.auctionhouseapp.Objects.AuctionHouse
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils


class HouseProfile : Fragment() {

    var my_house = AuctionHouse()
    lateinit var house_img:ImageView
    lateinit var btn_confirm:Button
    lateinit var image:Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_house, container, false)
        val txt_profile_name  = view.findViewById<TextView>(R.id.txt_profile_name)
        val txt_profile_email = view.findViewById<TextView>(R.id.txt_profile_email)
        val txt_my_total_cash = view.findViewById<TextView>(R.id.txt_my_total_cash)
        val txt_rating  = view.findViewById<TextView>(R.id.text_view_rating)
        val txt_raters  = view.findViewById<TextView>(R.id.text_view_no_raters)
        house_img = view.findViewById<ImageView>(R.id.imageView_house_profile)
        btn_confirm = view.findViewById<Button>(R.id.profileConfirm)

        txt_profile_name.setText(my_house.GetName())
        txt_profile_email.setText(my_house.GetEmail())
        txt_my_total_cash.setText(my_house.getCash().toString())
        txt_rating.setText(String.format("%.1f",my_house.Rating))
        txt_raters.setText(my_house.TotalRaters.toString())

        if(my_house.profile_img_url != null)
            this.context?.let {
                Glide.with(it)
                    .load(my_house.profile_img_url)
                    .into(view.findViewById<ImageView>(R.id.imageView_house_profile))
            }

        btn_confirm.visibility = View.INVISIBLE

        house_img.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent,100)
        }

        btn_confirm.setOnClickListener {
            UploadImage()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            image = data?.data!!
            house_img.setImageURI(image)
            btn_confirm.visibility = View.VISIBLE
        }
    }

    fun UploadImage(){
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var fileName = image.toString()
        fileName = fileName.split("/").last()
        val storageRef = FirebaseUtils.firebaseStore.getReference("Photos/$fileName")
        storageRef.putFile(image).
        addOnSuccessListener {
            storageRef.downloadUrl.addOnCompleteListener {
                my_house.profile_img_url = it.result.toString()
                StoreURLinDB(it.result.toString(),progressDialog)
            }
        }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun StoreURLinDB(URL:String,progressDialog:ProgressDialog){
        FirebaseUtils.houseCollectionRef
            .document(my_house.GetUID())
            .update(Constants.PROFILE_URL,URL)
            .addOnSuccessListener {
                if(progressDialog.isShowing) progressDialog.dismiss()
            }
            .addOnFailureListener {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

}