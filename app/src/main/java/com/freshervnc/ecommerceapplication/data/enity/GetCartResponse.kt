package com.freshervnc.ecommerceapplication.data.enity

import androidx.annotation.Keep
import com.freshervnc.ecommerceapplication.data.model.ResponseCart

@Keep
data class GetCartResponse (
    val code : String? = null,
    val message : List<String>?,
    val response : ResponseCart
)
