package com.example.auctionhouseapp.Activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.example.auctionhouseapp.Utils.FirebaseUtils.houseCollectionRef
import com.example.auctionhouseapp.Utils.FirebaseUtils.usersCollectionRef
import com.google.firebase.firestore.CollectionReference


class SignUpActivity : AppCompatActivity() {


    private lateinit var fullName: EditText
    private lateinit var emailEt: EditText
    private lateinit var passEt: EditText
    private lateinit var CpassEt: EditText
    private lateinit var phoneNoEt: EditText
    private lateinit var addressEt: EditText
    private var userType = -1

    private val customerCollectionRef = Firebase.firestore.collection("Customers")
    private val houseCollectionRef = Firebase.firestore.collection("Houses")
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var progressDialog:ProgressDialog

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signUpBtn = findViewById<Button>(R.id.signUpBtn_signUpPage)
        fullName = findViewById(R.id.nameEt_signUpPage)
        emailEt = findViewById(R.id.emailEt_signUpPage)
        passEt = findViewById(R.id.PassEt_signUpPage)
        CpassEt = findViewById(R.id.cPassEt_signUpPage)
        phoneNoEt = findViewById(R.id.phoneNoEt_signUpPage)
        addressEt = findViewById(R.id.addressEt_signUpPage)
        val radioGroup = findViewById<RadioButton>(R.id.radioGroup) as RadioGroup
        val signInTv = findViewById<TextView>(R.id.signInTv_signUpPage)

        radioGroup.setOnCheckedChangeListener { group, ID ->
            when (ID) {
                R.id.customer -> {
                    userType = 0
                }
                R.id.auction_house -> {
                    userType = 1
                }
            }
        }

        progressDialog = ProgressDialog(this)

        textAutoCheck()

        signInTv.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        signUpBtn.setOnClickListener {
            checkInput()
        }
    }

    private fun textAutoCheck() {

        fullName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (fullName.text.isEmpty()){
                    fullName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (fullName.text.length >= 4){
                    fullName.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                fullName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count >= 4){
                    fullName.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        emailEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (emailEt.text.isEmpty()){
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (emailEt.text.matches(emailPattern.toRegex())) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (emailEt.text.matches(emailPattern.toRegex())) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        passEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (passEt.text.isEmpty()){
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (passEt.text.length > 5){
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count > 5){
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        CpassEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (CpassEt.text.isEmpty()){
                    CpassEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (CpassEt.text.toString() == passEt.text.toString()){
                    CpassEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                CpassEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (CpassEt.text.toString() == passEt.text.toString()){
                    CpassEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        phoneNoEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (phoneNoEt.text.isEmpty()){
                    phoneNoEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (phoneNoEt.text.length == 10){
                    phoneNoEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                phoneNoEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count >= 4){
                    phoneNoEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

        addressEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (addressEt.text.isEmpty()){
                    addressEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (addressEt.text.length >= 4){
                    addressEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                addressEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (count >= 4){
                    addressEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                }
            }
        })

    }

    private fun checkInput() {
        if (fullName.text.isEmpty()){
            toast("Name can't be empty!")
            return
        }
        if (emailEt.text.isEmpty()){
            toast("Email can't be empty!")
            return
        }

        if (!emailEt.text.matches(emailPattern.toRegex())) {
            toast("Enter Valid Email")
            return
        }
        if(passEt.text.isEmpty()){
            toast("Password can't be empty!")
            return
        }
        if (passEt.text.toString() != CpassEt.text.toString()){
            toast("Password not Match")
            return
        }
        if (phoneNoEt.text.isEmpty()){
            toast("Phone No' can't be empty!")
            return
        }
        if (addressEt.text.isEmpty()){
            toast("Address can't be empty!")
            return
        }
        if(userType == -1) {
            toast("Please Select Any One!")
            return
        }

        signIn()
    }



    private fun signIn() {

        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating Account")
        progressDialog.show()

        val emailV: String = emailEt.text.toString()
        val passV: String = passEt.text.toString()
        val fullname : String = fullName.text.toString()
        val address : String = addressEt.text.toString()
        val phone : String = phoneNoEt.text.toString()

        /*create a user*/
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailV,passV)

            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.setMessage("Save User Data")

                    val userHashMap : HashMap<String, Any>
                            = HashMap<String, Any> ()
                    userHashMap[Constants.USER_NAME] = fullname
                    userHashMap[Constants.USER_EMAIL] = emailV
                    userHashMap[Constants.USER_TYPE] = userType
                    userHashMap[Constants.USER_PHONE] = phone
                    userHashMap[Constants.USER_ADDR] = address
                    userHashMap[Constants.USERID] = FirebaseAuth.getInstance().uid.toString()
                    userHashMap[Constants.USER_CASH] = 0

                    if (userType == 0) {
                        storeData(userHashMap,customerCollectionRef,userType)
                        val intent = Intent(applicationContext, CustomerMainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        userHashMap[Constants.HOUSE_RATING_SUM] = 3
                        userHashMap[Constants.HOUSE_NUM_RATERS] = 1
                        storeData(userHashMap,houseCollectionRef,userType)
                        val intent = Intent(applicationContext, HouseActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    // check if user document is no
                    progressDialog.dismiss()
                    toast("failed to Authenticate !")
                }
            }

    }



    private fun storeData(user: HashMap<String, Any>, collectionRef: CollectionReference, type:Int) {
        try {
            var isDataStoreSuccessful = false
            var isTypeStoreSuccessful = false
            FirebaseAuth.getInstance().currentUser?.let {
                collectionRef.document(it.uid).set(user)
                    .addOnSuccessListener {
                        isDataStoreSuccessful = true
                        if (isTypeStoreSuccessful) {
                            toast("Data Saved")
                            progressDialog.dismiss()
                        }

                    }
                    .addOnFailureListener { toast("Sign Up Failed !!") }
                usersCollectionRef.document(it.uid).set(
                    mapOf(
                        Constants.USER_TYPE to type
                    )
                )
                    .addOnSuccessListener {
                        isTypeStoreSuccessful = true
                        if (isDataStoreSuccessful) {
                            toast("Data Saved")
                            progressDialog.dismiss()
                        }
                    }
                    .addOnFailureListener { toast("Sign Up Failed !!") }
            }

        } catch (e:Exception){
                toast(""+ e.message.toString())
                progressDialog.dismiss()
        }
    }


}
