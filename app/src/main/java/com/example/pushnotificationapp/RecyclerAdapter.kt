package com.example.pushnotificationapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(val userList: List<User>,val onclick: (token :String)->Unit) : RecyclerView.Adapter<RecyclerAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name : TextView = view.findViewById<TextView>(R.id.user_email)
        val sendIcon : ImageView = view.findViewById<ImageView>(R.id.send_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.name.text = userList[position].email
        holder.sendIcon.setOnClickListener{
            onclick(userList[position].token)
        }
    }

    override fun getItemCount() = userList.size



}