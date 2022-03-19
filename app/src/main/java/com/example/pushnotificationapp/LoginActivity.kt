package com.example.pushnotificationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private val TAG = "MainActivity"


    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var loginButton: Button
    lateinit var auth: FirebaseAuth
    lateinit var mdbRef: DatabaseReference


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        auth = Firebase.auth

        loginButton.setOnClickListener {
            val email_text = email.text.toString()
            val pass_text = password.text.toString()

            if (email_text.isNotEmpty() && pass_text.isNotEmpty()) {
                login(email_text, pass_text)
            } else {

                Toast.makeText(
                    this,
                    "please make sure you enter correct login details ",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    }


    private fun login(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    addUsertoDB(email, user!!.uid)
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }


    }


    fun initView() {
        email = findViewById(R.id.email_text)
        password = findViewById(R.id.pass_text)
        loginButton = findViewById(R.id.btn_login)
    }

    fun updateUI() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun addUsertoDB(email: String, uid: String) {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    val token = task.result
                    mdbRef = FirebaseDatabase.getInstance().reference
                    mdbRef.child("user").child(uid).setValue(User(uid, email, token))

                }

            }


    }




}