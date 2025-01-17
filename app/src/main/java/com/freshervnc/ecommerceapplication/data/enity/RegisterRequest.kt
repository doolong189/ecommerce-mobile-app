package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class RegisterRequest(
    val name: String? = "",
    var address : String? = "" ,
    var password:String? = "",
    var email:String? = "",
    var phone:String? = "",
    var image: String? = "" ,
    val loc: List<Double>?  ,
    val token : String? = ""
)