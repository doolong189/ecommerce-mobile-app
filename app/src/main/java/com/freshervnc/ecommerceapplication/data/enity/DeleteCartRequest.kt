package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep

@Keep
data class DeleteCartRequest (
    val idUser : String? = "",
    val idProduct : String? = ""
)