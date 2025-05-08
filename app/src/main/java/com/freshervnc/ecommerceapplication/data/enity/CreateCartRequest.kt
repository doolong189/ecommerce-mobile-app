package com.freshervnc.ecommerceapplication.data.enity


data class CreateCartRequest(
    val idProduct : String? = "",
    val idUser : String? = "",
    val quantity : Int? = 0
)
