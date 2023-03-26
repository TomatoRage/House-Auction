package com.example.auctionhouseapp.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.auctionhouseapp.Objects.ImagesSharedPref
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.FirebaseUtils
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.google.firebase.auth.FirebaseAuth
import com.example.auctionhouseapp.adapter.AddOns.loadingDialog
import com.example.auctionhouseapp.Utils.FirebaseUtils.customerCollectionRef
import com.example.auctionhouseapp.Utils.FirebaseUtils.houseCollectionRef
import com.example.auctionhouseapp.Utils.FirebaseUtils.itemsCollectionRef
import com.example.auctionhouseapp.Utils.FirebaseUtils.usersCollectionRef
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInBtn: Button
    lateinit var emailEt: EditText
    lateinit var passEt: EditText
    lateinit var loadingDialog: loadingDialog
    lateinit var emailError:TextView
    lateinit var passwordError:TextView
    lateinit var dialog:AlertDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initiate local memory for items; photos
        //ImagesSharedPref.init(getApplicationContext())
        val signUpTv = findViewById<TextView>(R.id.signUpTv)
        signInBtn = findViewById(R.id.loginBtn)
        emailEt = findViewById(R.id.emailEt)
        passEt = findViewById(R.id.PassEt)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)

        textAutoCheck()

        loadingDialog = loadingDialog(this)

        signUpTv.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signInBtn.setOnClickListener {
            checkInput()

        }


    }

    private fun textAutoCheck() {

        emailEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (emailEt.text.isEmpty()){
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                    emailError.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

                emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)
                    emailError.visibility = View.GONE
                }
            }
        })

        passEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (passEt.text.isEmpty()){
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                }
                else if (passEt.text.length > 4){
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
                passwordError.visibility = View.GONE
                if (count > 4){
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic_check
                    ), null)

                }
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkInput() {

        if (emailEt.text.isEmpty()){
            emailError.visibility = View.VISIBLE
            emailError.text = "Email Can't be Empty"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Enter Valid Email"
            return
        }
        if(passEt.text.isEmpty()){
            passwordError.visibility = View.VISIBLE
            passwordError.text = "Password Can't be Empty"
            return
        }

        if ( passEt.text.isNotEmpty() && emailEt.text.isNotEmpty()){
            emailError.visibility = View.GONE
            passwordError.visibility = View.GONE
            signInUser()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun signInUser() {

        //loadingDialog.startLoadingDialog()a
        signInEmail = emailEt.text.toString().trim()
        signInPassword = passEt.text.toString().trim()
        if(FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(signInEmail, signInPassword)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    //loadingDialog.dismissDialog()
                    checkUser(::getUserName)

                } else {
                    toast("sign in failed")
                }
            }
    }


    fun fetchUserType(ToPerform: (type: Int) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.let {
            usersCollectionRef
                .document(it.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null) {
                        val userType = (doc.data?.get("Type") as Long).toInt()
                        if (userType != -1)
                            ToPerform(userType)
                    }
                }
                .addOnFailureListener {exception ->
                    Log.d("LoginActivity", "-E- while fetching user type", exception)

                }
        }

    }

    fun checkUser(ToPerform: (type:Int) -> Unit) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater. inflate (R.layout.fragment_auction_days_spinner,null)
        builder.setView (dialogView)
        builder.setCancelable (false)
        dialog = builder.create()
        dialog.show()
        Handler().postDelayed({
            fetchUserType(ToPerform)
        }, 500)
    }

    fun fetchUserName(collectionName:String,ToPerform: (type:Int,name:String) -> Unit,type: Int) {
        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseFirestore.getInstance().collection(collectionName)
                .document(it.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null) {
                        val name = (doc.data?.get("Full Name") as String)
                        ToPerform(type,name)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("LoginActivity", "-E- while fetching user type", exception)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserName(userType: Int) {
        if (userType == 0) {
            fetchUserName(Constants.CUSTOMERS_COLLECTION,::goToNextActivity,0)
        } else if (userType == 1) {
            fetchUserName(Constants.HOUSES_COLLECTION,::goToNextActivity,1)
        } else {
            toast("Error While Reading User Type!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun goToNextActivity(type: Int, name: String) {
//        AsyncTask.execute {
//            fetchImagesFromCloud()
//        }
        if(type == 0) {
            val intent = Intent(applicationContext,CustomerMainActivity::class.java)
            //intent.putExtra("User Name", name)
            //intent.putExtra("User Email", emailEt.text.toString())
            dialog.dismiss()
            startActivity(intent)
            finish()
        } else if (type == 1) {
            val intent = Intent(applicationContext,HouseActivity::class.java)
            //intent.putExtra("User Name", name)
            //intent.putExtra("User Email", emailEt.text.toString())
            dialog.dismiss()
            startActivity(intent)
            finish()
        } else {
            throw Exception("-E- Failed to go to next activity")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchImagesFromCloud() {
        itemsCollectionRef
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    val itemImages:ArrayList<String> = doc.data.get("Photos") as ArrayList<String>
                    for (imageID in itemImages) {
                        if (ImagesSharedPref.contain(imageID)) continue
                        ImagesSharedPref.storeImage(imageID)
                    }
                }
            }.addOnFailureListener {
                Log.i("LoginActivity.kt", "-E- while fetching images")
            }
    }

}

