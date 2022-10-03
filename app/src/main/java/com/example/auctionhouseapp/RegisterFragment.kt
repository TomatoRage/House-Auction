package com.example.auctionhouseapp

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.findFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var  cnfpassword : EditText
    private lateinit var nickname : EditText
    private lateinit var mobile : EditText
    private lateinit var address : EditText
    private lateinit var fAuth : FirebaseAuth
    private lateinit var btn_register_reg : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        username = view.findViewById(R.id.reg_user_name)
        password = view.findViewById(R.id.reg_password)
        cnfpassword = view.findViewById(R.id.reg_password_confirm)
        nickname = view.findViewById(R.id.reg_nickname)
        mobile = view.findViewById(R.id.reg_mobile)
        address = view.findViewById(R.id.reg_address)
        fAuth = Firebase.auth
        btn_register_reg = view.findViewById(R.id.btn_register_reg)




        view.findViewById<Button>(R.id.btn_login_reg).setOnClickListener {
            val navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(LoginFragment(),false)

        }
        view.findViewById<Button>(R.id.btn_register_reg).setOnClickListener {
            validateEmptyForm()
        }
        return view
    }

    private fun firebaseSignUp() {
        btn_register_reg.isEnabled = false
        btn_register_reg.alpha = 0.5f
        fAuth.createUserWithEmailAndPassword(username.text.toString(),
            password.text.toString()).addOnCompleteListener{
                task ->
                if(task.isSuccessful) {
                    val navAucHouseList = activity as FragmentNavigation
                    navAucHouseList.navigateFrag(AuctionHouseListFragment(),addToStack = true)
                } else {
                    btn_register_reg.isEnabled = true
                    btn_register_reg.alpha = 1.0f
                    Toast.makeText(context,task.exception?.message,Toast.LENGTH_SHORT).show()
                }
        }



    }
    private fun validateEmptyForm() {
        val icon = AppCompatResources.getDrawable(requireContext(),R.drawable.warning_icon)
        when{
            TextUtils.isEmpty(username.text.toString().trim())->{
                username.setError("Please Enter Username")
            }
            TextUtils.isEmpty(password.text.toString().trim())->{
                password.setError("Please Enter Password")
            }
            TextUtils.isEmpty(cnfpassword.text.toString().trim())->{
                cnfpassword.setError("Please Enter Password Again")
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() &&
                    cnfpassword.text.toString().isNotEmpty() ->
            {
                if (username.text.toString().matches(Regex("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})\$"))){
                    if(password.text.toString().length >= 5){
                        if(password.text.toString() == cnfpassword.text.toString()) {
                            if(nickname.text.toString().matches(Regex("[a-zA-Z0-9._-]+"))) {
                                if(mobile.text.toString().matches(Regex("[0-9]+")) && mobile.text.toString().length == 10){
                                    if(address.text.toString().matches(Regex("[a-zA-Z0-9]+"))) {
                                        firebaseSignUp()
                                    } else {
                                        address.setError("Please Enter Valid Address")
                                    }
                                } else {
                                    mobile.setError("Please Enter Valid Number")
                                }
                            } else {
                                nickname.setError("Please Enter Valid Nickname Without Special Chars")
                            }
                        } else {
                            cnfpassword.setError("Password didn't match")
                        }
                    } else {
                        password.setError("Please Enter at least 5 characters",icon)
                    }
                } else {
                    username.setError("Please Enter Valid UserName")
                }
            }
        }

    }


}