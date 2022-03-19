package com.example.pushnotificationapp

data class User(
    val uid: String,
    val email: String,
    val token : String
){
    constructor() : this("","","") {


    }

}
