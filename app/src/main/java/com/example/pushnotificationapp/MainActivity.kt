package com.example.pushnotificationapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val TAB = "MainActivity"
    lateinit var mdbRef: DatabaseReference
    lateinit var mAdapter: RecyclerAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var mAuth: FirebaseAuth
    var userList: MutableList<User> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        mAuth = Firebase.auth
        getAllUser()

        mAdapter = RecyclerAdapter(userList, onClick)

        recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

    }

    val onClick: (token: String) -> Unit = { token ->
        val title = "testTitle"
        val message = "Notification Message"
        PushNotification(
            NotificationData(title, message),
            token
        ).also {
            sendNotification(it)
        }
    }

    private fun getAllUser() {
        FirebaseDatabase.getInstance().reference
            .child("user")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    userList.clear()
                    for (postsnapshort in snapshot.children) {
                        val currentUser = postsnapshort.getValue(User::class.java)
                        currentUser?.let {
                            if (it.uid != mAuth.currentUser?.uid ?: false){
                                userList.add(currentUser)
                            }
                        }

                    }

                    mAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })


    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.e(TAG, "${response.body()}")
                } else {

                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendNotification: ${e.toString()}")
            }


        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        MenuInflater(this).inflate(R.menu.logout_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                clearToken(mAuth.currentUser!!.uid)
                startActivity(Intent(this, LoginActivity::class.java))
                mAuth.signOut()
                finish()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun clearToken(userId: String) {
        FirebaseDatabase.getInstance()
            .getReference("user")
            .child(userId)
            .removeValue()

    }

}