package com.example.auctionhouseapp.Activities

import android.content.Intent
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.auctionhouseapp.R
import com.example.auctionhouseapp.Utils.Constants
import com.example.auctionhouseapp.Utils.Extensions.toast
import com.google.firebase.auth.FirebaseAuth
import com.example.auctionhouseapp.AddOns.loadingDialog
import com.example.auctionhouseapp.Utils.FirebaseUtils.userCollectionRef


class LoginActivity : AppCompatActivity() {

    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInBtn: Button
    lateinit var emailEt: EditText
    lateinit var passEt: EditText
    var userType = -1

    lateinit var loadingDialog: loadingDialog

    lateinit var emailError:TextView
    lateinit var passwordError:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
                    checkUser(::goToNextActivity)

                } else {
                    toast("sign in failed")
                }
            }
    }



    fun checkUser(ToPerform: () -> Unit) {
        FirebaseAuth.getInstance().currentUser?.let {
            userCollectionRef
                .document(it.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null) {
                        userType = (doc.data?.get(Constants.USER_TYPE) as Long).toInt()
                        if(userType!= -1)
                            ToPerform()
                    }

                }
                .addOnFailureListener { exception ->
                    Log.d("LoginActivity", "Requested Items data read failed with", exception)
                }
        }
    }

    fun goToNextActivity() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater. inflate (R.layout.fragment_auction_days_spinner,null)
        builder.setView (dialogView)
        builder.setCancelable (false)
        val dialog = builder.create()
        dialog.show()
        Handler().postDelayed({
            if (userType == 0) {
                val intent = Intent(applicationContext, CustomerMainActivity::class.java)
                startActivity(intent)
                finish()
            } else if (userType == 1) {
                val intent = Intent(applicationContext, HouseActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                toast("Error While Reading User Type!")
            }
        }, 500)



    }

}
