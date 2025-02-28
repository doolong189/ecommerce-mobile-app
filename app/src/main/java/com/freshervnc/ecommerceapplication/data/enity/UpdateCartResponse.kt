package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.model.ResponseCart

@Keep
data class UpdateCartResponse (
    val code : String? = null,
    val message : List<String>?,
    val response : ResponseCart
)
