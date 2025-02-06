package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.UserInfo

@Keep
data class AddCartRequest(
    val idProduct : String? = "",
    val idUser : String? = "",
    val quantity : Int? = 0
)
