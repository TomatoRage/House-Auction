package com.example.auctionhouseapp

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var fAuth : FirebaseAuth
    private lateinit var btn_login : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)
        username = view.findViewById(R.id.login_user_name)
        password = view.findViewById(R.id.login_password)
        fAuth = Firebase.auth
        btn_login = view.findViewById(R.id.btn_login)

        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(),false)

        }
        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            validateForm()
        }
        return view
    }


    private fun firebaseSignIn() {
        btn_login.isEnabled = false
        btn_login.alpha = 0.5f
        fAuth.signInWithEmailAndPassword(username.text.toString(),
            password.text.toString()).addOnCompleteListener{
                task ->
                if(task.isSuccessful) {
                    val navAucHouseList = activity as FragmentNavigation
                    navAucHouseList.navigateFrag(AuctionHouseListFragment(),addToStack = true)
                } else {
                    btn_login.isEnabled = true
                    btn_login.alpha = 1.0f
                    Toast.makeText(context,task.exception?.message,Toast.LENGTH_SHORT).show()

                }
        }

    }
    private fun validateForm() {
        val icon = AppCompatResources.getDrawable(requireContext(),R.drawable.warning_icon)
        when{
            TextUtils.isEmpty(username.text.toString().trim())->{
                username.setError("Please Enter Username")
            }
            TextUtils.isEmpty(password.text.toString().trim())->{
                password.setError("Please Enter Password")
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() ->
            {
                if (username.text.toString().matches(Regex("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})\$"))){
                   firebaseSignIn()
                } else {
                    username.setError("Please Enter Valid Username")
                }
            }
        }


    }


}