package com.freshervnc.ecommerceapplication.data.enity


data class RegisterRequest(
    val name: String? = "",
    var address : String? = "" ,
    var password:String? = "",
    var email:String? = "",
    var phone:String? = "",
    var image: String? = "" ,
    val loc: List<Double>? = listOf(0.0,0.0),
    val token : String? = ""
)