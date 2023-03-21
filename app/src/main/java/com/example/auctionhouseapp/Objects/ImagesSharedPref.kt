package com.example.auctionhouseapp.Objects

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import java.io.ByteArrayOutputStream


object ImagesSharedPref {
    private var mSharedPref: SharedPreferences? = null
    private lateinit var imageID2BytesMap: HashMap<String,ByteArray>

    private fun ImagesSharedPref() {}
    fun init(context: Context) {
        if (mSharedPref == null) {
            mSharedPref =
                context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE)
            imageID2BytesMap = HashMap<String,ByteArray>()
        }
    }

    fun read(key: String?): String? {
        if (!contain(key)) {
            throw Exception("Can not read Image From Shared Pref")
        }
        val defValue = "Error"
        return mSharedPref!!.getString(key, defValue)
    }

    fun contain(key: String?): Boolean {
        return mSharedPref?.contains(key) ?: false
    }

    fun write(key: String?, value: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decodeImages() {
        mSharedPref?.all?.forEach{ imageID ->
            val imageID_str = imageID.toString()
            if (!imageID2BytesMap.containsKey(imageID_str)) {
                fetchImage(imageID as String)?.let {
                    imageID2BytesMap.put(
                        imageID.toString(),
                        it
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun storeImage(imageID: String) {
        AsyncTask.execute {
            val MaxImageSize: Long = 1080 * 1080 * 1000
            FirebaseUtils.firebaseStore.reference
                .child(Constants.STORAGE_ITEM + imageID)
                .getBytes(MaxImageSize)
                .addOnSuccessListener { Bytes ->
                    val Bitmap = BitmapFactory.decodeByteArray(Bytes, 0, Bytes.size)
                    var Stream = ByteArrayOutputStream()
                    Bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, Stream)
                    write(imageID, java.util.Base64.getEncoder().encodeToString(Stream.toByteArray()))
                }.addOnFailureListener {
                    Log.i("ImageSharedPref", "-E- while storing image in ImagesSharedPref")
                }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchImage(imageID: String): ByteArray? {
        if(imageID2BytesMap.containsKey(imageID)) {
            return imageID2BytesMap.get(imageID)
        }

        val image = read(imageID) as String
        val imageBytesArr = java.util.Base64.getDecoder().decode(image)
        return imageBytesArr
    }

}