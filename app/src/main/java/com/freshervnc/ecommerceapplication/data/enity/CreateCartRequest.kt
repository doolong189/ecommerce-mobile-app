package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class CreateCartRequest(
    val idProduct : String? = "",
    val idUser : String? = "",
    val quantity : Int? = 0
)
