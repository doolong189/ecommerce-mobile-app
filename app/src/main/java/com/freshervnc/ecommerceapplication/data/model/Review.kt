package com.freshervnc.ecommerceapplication.data.model


data class  Review (
    val title : String,
    val date : String,
    val rating : Float,
    val idUser : UserInfo?,
    val idProduct : Product
)