package com.freshervnc.ecommerceapplication.data.model

data class UserInfo(
    var _id : String ,
    var name:String ,
    var address : String ,
    var password:String,
    var email:String,
    var phone:String,
    var image: String ,
    val loc: List<Double>? ,
    val token : String
)