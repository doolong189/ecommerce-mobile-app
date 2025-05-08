package com.freshervnc.ecommerceapplication.data.enity


data class UpdateCartRequest(
    val idUser : String? = "",
    val idProduct : String? = "",
    val quantity : Int? = 0
)