package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class UpdateCartRequest(
    val idUser : String? = "",
    val idProduct : String? = "",
    val quantity : Int? = 0
)